
local vertex_shader_position_texture_color = [[
    attribute vec4 a_position;
    attribute vec2 a_texCoord;
    attribute vec4 a_color;
    
    #ifdef GL_ES
    varying lowp vec4 v_fragmentColor;
    varying mediump vec2 v_texCoord;
    #else
    varying vec4 v_fragmentColor;
    varying vec2 v_texCoord;
    #endif
    
    void main()
    {
        gl_Position = CC_MVPMatrix * a_position;
        v_fragmentColor = a_color;
        v_texCoord = a_texCoord;
    }
]]

-- 滤波处理函数
local dip_filter = [[
    vec4 dip_filter( mat3 _filter, sampler2D tex, vec2 xy, vec2 texSize )
    {
        mat3 _filter_x = mat3( -1.0, 0.0, 1.0,
                               -1.0, 0.0, 1.0,
                               -1.0, 0.0, 1.0 );
        mat3 _filter_y = mat3( -1.0, -1.0, -1.0,
                               0.0, 0.0, 0.0,
                               1.0, 1.0, 1.0 );

        vec4 final_color = vec4( 0.0, 0.0, 0.0, 0.0 );

        for( int i=0; i < 3; i++ )
        {
            for( int j=0; j < 3; j++ )
            {
                vec2 xy_new = vec2( xy.x + _filter_x[i][j], xy.y + _filter_y[i][j] );
                vec2 uv = vec2( xy_new.x / texSize.x, xy_new.y / texSize.y );

                vec4 temp_color = texture2D( tex, uv ) * _filter[i][j];
                final_color = final_color + temp_color;
            }
        }

        return final_color;
    }
]]

local filter_param_smooth = [[
    mat3 _filter = mat3( 1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0,
                         1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0,
                         1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0 );
]]
local filter_param_gaussian = [[
    mat3 _filter = mat3( 1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0,
                         2.0 / 16.0, 4.0 / 16.0, 2.0 / 16.0,
                         1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0 );
]]
local filter_param_laplacian = [[
    mat3 _filter = mat3( 1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0,
                         2.0 / 16.0, 4.0 / 16.0, 2.0 / 16.0,
                         1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0 );
]]
local filter_param_stroke = [[
    mat3 _filter = mat3( -0.5, -1.0, 0.0,
                         1.0, 0.0, 1.0,
                         -0.0, 1.0, 0.5 );
]]

local custom_shaders = {
    {
        shader_name = 'position_texture_color_gray',
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                float f = 0.3 * color.r + 0.6 * color.g + 0.1 * color.b;
                gl_FragColor = vec4( f, f, f, color.a );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_change_color',
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                gl_FragColor = vec4( color.r * CC_Custom.x, color.g * CC_Custom.y, color.b * CC_Custom.z, color.a );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_change_color_ex',
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
        #ifdef GL_ES
        precision highp float;
        #endif

        varying vec4 v_fragmentColor;
        varying vec2 v_texCoord;
        uniform sampler2D CC_Texture0;
        uniform vec4 CC_Custom;

        void main()
        {
            vec4 t_color = texture2D( CC_Texture0, v_texCoord );
            if( t_color.r == 1.0 && t_color.g == 1.0 && t_color.b == 1.0 )
                t_color.rgb = CC_Custom.rgb;

            vec4 color = v_fragmentColor * t_color;
            gl_FragColor = vec4( color.r, color.g, color.b, color.a );
        }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_texture_flow',
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
        #ifdef GL_ES
        precision highp float;
        #endif

        varying vec4 v_fragmentColor;
        varying vec2 v_texCoord;
        uniform sampler2D CC_Texture0;
        uniform vec4 CC_Custom;

        void main()
        {
            vec2 uv = v_texCoord.xy + CC_Custom.xy;
            vec4 color = v_fragmentColor * texture2D( CC_Texture0, uv );
            gl_FragColor = vec4( color.r, color.g, color.b, color.a );
        }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_HDR',                 -- 伪 HDR
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                float gray = 0.3 * color.r + 0.59 * color.g + 0.11 * color.b;

                float k = 2.0;
                float b = ( 4.0 * k - 1.0 );
                float a = 1.0 - b;
                float f = gray * ( a * gray + b );

                gl_FragColor = f * color;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_relief',              -- 浮雕效果
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                vec2 upLeftUV = vec2( v_texCoord.x - 1.0 / CC_Custom.x, v_texCoord.y - 1.0 / CC_Custom.y );

                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                vec4 upLeftColor = texture2D( CC_Texture0, upLeftUV );

                vec4 delColor = color - upLeftColor;

                float gray = 0.3 * delColor.r + 0.59 * delColor.g + 0.11 * delColor.b;
                gray += 0.5;
                gray *= color.a;

                gl_FragColor = vec4( gray, gray, gray, color.a );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_mosaics_box',              -- 马赛克
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                vec2 intXY = vec2( v_texCoord.x * CC_Custom.x, v_texCoord.y * CC_Custom.y );
                vec2 mXY = vec2( intXY.x / CC_Custom.z * CC_Custom.z, intXY.y / CC_Custom.w * CC_Custom.w );
                vec2 uv = vec2( mXY.x / CC_Custom.x, mXY.y / CC_Custom.y );

                //gl_FragColor = v_fragmentColor * texture2D( CC_Texture0, uv );
                gl_FragColor = texture2D( CC_Texture0, uv );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_mosaics_round',              -- 马赛克
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                vec2 intXY = vec2( v_texCoord.x * CC_Custom.x, v_texCoord.y * CC_Custom.y );
                vec2 mXY = vec2( intXY.x / CC_Custom.z * CC_Custom.z, intXY.y / CC_Custom.w * CC_Custom.w ) + 0.5 * CC_Custom.zw;

                vec2 delXY = mXY - intXY;
                float len = length( delXY );

                if( len < 0.5 * CC_Custom.z )
                {
                    vec2 uv = vec2( mXY.x / CC_Custom.x, mXY.y / CC_Custom.y );
                    gl_FragColor = texture2D( CC_Texture0, uv );
                }
                else
                {
                    gl_FragColor = texture2D( CC_Texture0, v_texCoord );
                }
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_sharpen_fuzzy_smooth',              -- 锐化模糊 平滑
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;
        ]] .. dip_filter .. [[
            void main()
            {
                vec2 intXY = vec2( v_texCoord.x * CC_Custom.x, v_texCoord.y * CC_Custom.y );

        ]] .. filter_param_smooth .. [[

                gl_FragColor = dip_filter( _filter, CC_Texture0, intXY, CC_Custom.xy );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_sharpen_fuzzy_gaussian',              -- 锐化模糊 高斯
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;
        ]] .. dip_filter .. [[
            void main()
            {
                vec2 intXY = vec2( v_texCoord.x * CC_Custom.x, v_texCoord.y * CC_Custom.y );

        ]] .. filter_param_gaussian .. [[

                gl_FragColor = dip_filter( _filter, CC_Texture0, intXY, CC_Custom.xy );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_sharpen_fuzzy_laplacian',              -- 锐化模糊 拉普拉斯
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;
        ]] .. dip_filter .. [[
            void main()
            {
                vec2 intXY = vec2( v_texCoord.x * CC_Custom.x, v_texCoord.y * CC_Custom.y );

        ]] .. filter_param_laplacian .. [[

                gl_FragColor = dip_filter( _filter, CC_Texture0, intXY, CC_Custom.xy );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_stroke',              -- 描边
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;
        ]] .. dip_filter .. [[
            void main()
            {
                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                vec2 intXY = vec2( v_texCoord.x * CC_Custom.x, v_texCoord.y * CC_Custom.y );

        ]] .. filter_param_stroke .. [[

                vec4 delColor = dip_filter( _filter, CC_Texture0, intXY, CC_Custom.xy );
                float delGray = 0.3 * delColor.r + 0.59 * delColor.g + 0.11 * delColor.b;

                if( delGray < 0.0 )
                    delGray = -1.0 * delGray;

                delGray = ( 1.0 - delGray ) * color.a;

                gl_FragColor = vec4( delGray, delGray, delGray, color.a );
                //gl_FragColor = vec4( 1.0, 0.0, 0.0, 0.5 );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_wave',              -- 水波
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                float f = 0.3 * color.r + 0.6 * color.g + 0.1 * color.b;
                gl_FragColor = vec4( f, f, f, color.a );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_zoomblur',              -- 径向模糊
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {				      
				// 设置模糊度
				float BlurAmount = 0.09;
   
				vec4 c = vec4(0,0,0,0);
				for (int i = -7; i < 8; i++)
				{
					vec2 uv = vec2(v_texCoord.x + BlurAmount * (float(i) / 14.0), v_texCoord.y);
					c += v_fragmentColor * texture2D(CC_Texture0, uv);
				}
				c /= 15.0;
            
				gl_FragColor = c;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_zoomblur_scale_white',              -- 径向泛白
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
				vec2 Center = vec2(CC_Custom.x, CC_Custom.y);

				float time = CC_Custom.z;
				float scaletime = (1.0-time);
				if(scaletime < 0.1) scaletime = 0.1;
      
				// 设置模糊度
				float BlurAmount = 0.2 * time / 0.3; // 0 ~0.2
				if(BlurAmount > 0.2) BlurAmount = 0.2;
   
				vec4 c = vec4(0,0,0,0);
				vec2 uv = v_texCoord - Center;
				for (int i = 0; i < 15; i++)
				{
					float scale = 1.0 + BlurAmount * (float(i) / 14.0);
					c += v_fragmentColor * texture2D(CC_Texture0, uv * scale * scaletime + Center);
				}
				c /= 15.0;
				c += time;
            
				gl_FragColor = c;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_zoomblur',              -- 径向模糊
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
				vec2 Center = vec2(CC_Custom.x, CC_Custom.y);
				float BlurAmount = CC_Custom.z; // 模糊度 0 ~0.2
				if(BlurAmount > 0.2) BlurAmount = 0.2;
   
				vec4 c = vec4(0,0,0,0);
				vec2 uv = v_texCoord - Center;
				for (int i = 0; i < 15; i++)
				{
					float scale = 1.0 + BlurAmount * (float(i) / 14.0);
					c += v_fragmentColor * texture2D(CC_Texture0, uv * scale + Center);
				}
				c /= 15.0;
            
				gl_FragColor = c;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_alpha_glow',              -- 半透明处泛光
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {				   
				vec4 c = texture2D(CC_Texture0, v_texCoord);
				if(c.a > 0.0 && c.a < 1.0)	
				{
					c.r = CC_Custom.x * c.a;
					c.g = CC_Custom.y * c.a;
					c.b = CC_Custom.z * c.a;					
				}
            
				gl_FragColor = v_fragmentColor * c;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_brightness',              -- 亮度
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {			
				float B = CC_Custom.x; // 亮度
				float C = CC_Custom.y; // 对比度
				float k = tan( (45.0 + 44.0 * C) / 180.0 * 3.1415926 );
		   
				vec4 c = texture2D(CC_Texture0, v_texCoord);
				c.r = (c.r - 0.5 * (1.0 - B)) * k + 0.5 * (1.0 + B);
				c.g = (c.g - 0.5 * (1.0 - B)) * k + 0.5 * (1.0 + B);
				c.b = (c.b - 0.5 * (1.0 - B)) * k + 0.5 * (1.0 + B);
            
				gl_FragColor = v_fragmentColor * c;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_mask_layer',              -- 遮罩
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;
            uniform vec4 CC_Custom_Ex;

            void main()
            {
                if( ( v_texCoord.x > CC_Custom.x && v_texCoord.x < CC_Custom.x + CC_Custom.z && v_texCoord.y > CC_Custom.y && v_texCoord.y < CC_Custom.y + CC_Custom.w ) ||
                    ( v_texCoord.x > CC_Custom_Ex.x && v_texCoord.x < CC_Custom_Ex.x + CC_Custom_Ex.z && v_texCoord.y > CC_Custom_Ex.y && v_texCoord.y < CC_Custom_Ex.y + CC_Custom_Ex.w ) )
                {
					gl_FragColor = vec4( 0.0, 0.0, 0.0, 0.0 );
                }
				else
				{
					gl_FragColor = v_fragmentColor;
				}
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_replacement',              -- 颜色替换，我是使用在美术数字变色上
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                vec4 color = texture2D( CC_Texture0, v_texCoord );

                vec4 temp_color = color;
                if( color.r < 10.0 / 255.0 )
                {
                    temp_color = vec4( 0.0, 0.0, 0.0, 1.0 );
                }

                if( color.r > 45.0 / 255.0 && color.r < 55.0 / 255.0 )
                {
                    temp_color = vec4( 0.0, 0.0, 0.0, 0.0 );
                }

                if( color.r > 80.0 / 255.0 && color.r < 250.0 / 255.0 )
                {
                    temp_color.rgb = v_fragmentColor.rgb;
                    temp_color.a = 1.0;
                    //temp_color = vec4( 0.0, 1.0, 0.0, 1.0 );
                }

                gl_FragColor = temp_color;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        -- 战斗中使用，批量渲染，包括攻击箭头，血条，队长头标，特性等等
        shader_name = 'position_texture_color_fight_hp',
        vertex_shader = [[
            attribute vec4 a_position;
            attribute vec2 a_texCoord;
            attribute vec4 a_color;

            #ifdef GL_ES
            varying lowp vec4 v_fragmentColor;
            varying mediump vec2 v_texCoord;
            varying lowp vec4 v_fragmentPos;
            #else
            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            varying vec4 v_fragmentPos;
            #endif

            void main()
            {
                gl_Position = CC_MVPMatrix * a_position;
                v_fragmentColor = a_color;
                v_texCoord = a_texCoord;
                v_fragmentPos = a_position;
            }
        ]],
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            varying vec4 v_fragmentPos;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                if( v_texCoord.y == 0.0 )
                {
                    if( v_texCoord.x >= v_fragmentPos.z )
                    {
                        gl_FragColor.rgb = v_fragmentColor.rgb;
                        gl_FragColor.a = 1.0;
                    }
                    else
                    {
                        if( v_texCoord.x >= v_fragmentColor.a )
                        {
                            gl_FragColor = vec4( 0.8, 0.8, 0.8, 1.0 );
                        }
                        else
                        {
                            gl_FragColor = vec4( 0.0, 0.0, 0.0, 1.0 );
                        }
                    }
                }
                else
                {
                    gl_FragColor = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                }
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_fight_head',              -- 战斗中的头像
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                float len = length( vec2( v_texCoord.x - 0.5, v_texCoord.y - 0.5 ) );
                if( len <= 0.5 )
                {
                    gl_FragColor = texture2D( CC_Texture0, v_texCoord );

                    vec4 cover_color = vec4( 0.0, 0.0, 0.0, 0.0 );
                    if( v_fragmentColor.a < 0.5 )
                    {
                        gl_FragColor.r = gl_FragColor.r + v_fragmentColor.a;
                        gl_FragColor.g = gl_FragColor.g + v_fragmentColor.a;
                        gl_FragColor.b = gl_FragColor.b + v_fragmentColor.a;
                    }
                    else
                    {
                        gl_FragColor = gl_FragColor * v_fragmentColor;
                    }
                }
                else
                {
                    gl_FragColor = vec4( 0.0, 0.0, 0.0, 0.0 );
                }
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_fight_head_enemy',              -- 战斗中的头像
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                if( v_texCoord.x > CC_Custom.x && v_texCoord.x < CC_Custom.y && v_texCoord.y > CC_Custom.z && v_texCoord.y < CC_Custom.w )
                {
                    gl_FragColor = texture2D( CC_Texture0, v_texCoord );

                    vec4 cover_color = vec4( 0.0, 0.0, 0.0, 0.0 );
                    if( v_fragmentColor.a < 0.5 )
                    {
                        gl_FragColor.r = gl_FragColor.r + v_fragmentColor.a;
                        gl_FragColor.g = gl_FragColor.g + v_fragmentColor.a;
                        gl_FragColor.b = gl_FragColor.b + v_fragmentColor.a;
                    }
                    else
                    {
                        gl_FragColor = gl_FragColor * v_fragmentColor;
                    }
                }
                else
                {
                    gl_FragColor = vec4( 0.0, 0.0, 0.0, 0.0 );
                }
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_fight_boss_sketch',              -- 战斗 BOSS 剪影
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                gl_FragColor = texture2D( CC_Texture0, v_texCoord );
                gl_FragColor.r = 0.0;
                gl_FragColor.g = 0.0;
                gl_FragColor.b = 0.0;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_rich_string_color',              -- 颜色富文本
        vertex_shader = [[
            attribute vec4 a_position;
            attribute vec2 a_texCoord;
            attribute vec4 a_color;
            attribute vec4 a_color2;

            #ifdef GL_ES
            varying lowp vec4 v_fragmentColor;
            varying mediump vec2 v_texCoord;
            varying mediump vec4 v_fragmentColor2;
            #else
            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            varying vec4 v_fragmentColor2;
            #endif

            void main()
            {
                gl_Position = CC_MVPMatrix * a_position;
                v_fragmentColor = a_color;
                v_texCoord = a_texCoord;
                v_fragmentColor2 = a_color2;
            }
        ]],
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            varying vec4 v_fragmentColor2;
            uniform sampler2D CC_Texture0;
            uniform sampler2D CC_Texture1;

            void main()
            {
                vec4 color = texture2D( CC_Texture1, v_fragmentColor2.xy );
                gl_FragColor = v_fragmentColor * texture2D( CC_Texture0, v_texCoord ) * vec4( color.r, color.g, color.b, 1.0 );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
            { name = 'a_color2', index = kCCVertexAttrib_Color_2, },
        },
    },
    {
        shader_name = 'position_texture_color_model',
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                vec4 color = texture2D( CC_Texture0, v_texCoord );

                float r = color.r * ( 1.0 - v_fragmentColor.a ) + v_fragmentColor.r * v_fragmentColor.a;
                float g = color.g * ( 1.0 - v_fragmentColor.a ) + v_fragmentColor.g * v_fragmentColor.a;
                float b = color.b * ( 1.0 - v_fragmentColor.a ) + v_fragmentColor.b * v_fragmentColor.a;

                gl_FragColor.r = r * color.a;
                gl_FragColor.g = g * color.a;
                gl_FragColor.b = b * color.a;
                gl_FragColor.a = color.a;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_outer_glow',              -- 外发光
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );

                float param = CC_Custom.a;
                float r = ( ( CC_Custom.r * param ) * ( 1.0 - color.a ) + color.r ) * color.a;
                float g = ( ( CC_Custom.g * param ) * ( 1.0 - color.a ) + color.g ) * color.a;
                float b = ( ( CC_Custom.b * param ) * ( 1.0 - color.a ) + color.b ) * color.a;

                gl_FragColor = vec4( r, g, b, color.a );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_radar5dim',              -- 5维度雷达图 
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;
            uniform vec4 CC_Custom_Ex;

            // Determine whether point P in triangle ABC
            bool PointinTriangle(vec2 A, vec2 B, vec2 C, vec2 P)
            {
                vec3 cb = vec3(B.x - C.x, B.y - C.y, 0.0);
                vec3 ca = vec3(A.x - C.x, A.y - C.y, 0.0);
                vec3 pa = vec3(A.x - P.x, A.y - P.y, 0.0);
                vec3 pb = vec3(B.x - P.x, B.y - P.y, 0.0);
                vec3 pc = vec3(C.x - P.x, C.y - P.y, 0.0);

                bool inside = false;
                if ((length(cb) > 0.0) && (length(ca) > 0.0))
                    {
                        float crosab_ac = length(cross(cb, ca)) / 2.0;
                        float crospa_pb = length(cross(pa, pb)) / 2.0;
                        float crospb_pc = length(cross(pb, pc)) / 2.0;
                        float crospc_pa = length(cross(pc, pa)) / 2.0;

                        //return (crosab_ac >= (crospa_pb + crospb_pc + crospc_pa - 0.000001)) && (crosab_ac <= (crospa_pb + crospb_pc + crospc_pa + 0.000001));
                        inside = (crosab_ac >= (crospa_pb + crospb_pc + crospc_pa - 0.000001)) && (crosab_ac <= (crospa_pb + crospb_pc + crospc_pa + 0.000001));
                    }
                return inside;
            }

            void main()
            {
                //x，y交换转换成迪卡尔坐标


                float ratios[5]; 
                ratios[0] = CC_Custom.x;
                ratios[1] = CC_Custom.y;
                ratios[2] = CC_Custom.z;
                ratios[3] = CC_Custom.w;
                ratios[4] = CC_Custom_Ex.x;
                
                float const_radarR = 0.5;             //雷达半径
                vec2 const_origin = vec2(0.0, 0.0);  //
                int dim = 5;                          //维度

                //axis_x = (0.5, 0.0);

                float offset_degrees[5];
                offset_degrees[0] = radians(180.0);
                offset_degrees[1] = radians(-108.0);
                offset_degrees[2] = radians(-36.0);
                offset_degrees[3] = radians(36.0);
                offset_degrees[4] = radians(108.0);


                vec2 const_dimAxis[5];
                const_dimAxis[0] = vec2(0.5 * cos(radians(180.0)), -0.5 * sin(radians(180.0)));
                const_dimAxis[1] = vec2(0.5 * cos(radians(108.0)), -0.5 * sin(radians(108.0)));
                const_dimAxis[2] = vec2(0.5 * cos(radians(36.0)), -0.5 * sin(radians(36.0)));
                const_dimAxis[3] = vec2(0.5 * cos(radians(-36.0)), -0.5 * sin(radians(-36.0)));
                const_dimAxis[4] = vec2(0.5 * cos(radians(-108.0)), -0.5 * sin(radians(-108.0)));

                vec2 real_values[5];
                real_values[0] = vec2(const_dimAxis[0].x * ratios[0], const_dimAxis[0].y * ratios[0]);
                real_values[1] = vec2(const_dimAxis[1].x * ratios[1], const_dimAxis[1].y * ratios[1]);
                real_values[2] = vec2(const_dimAxis[2].x * ratios[2], const_dimAxis[2].y * ratios[2]);
                real_values[3] = vec2(const_dimAxis[3].x * ratios[3], const_dimAxis[3].y * ratios[3]);
                real_values[4] = vec2(const_dimAxis[4].x * ratios[4], const_dimAxis[4].y * ratios[4]);

                vec2 this_p = vec2(v_texCoord.y - 0.5, v_texCoord.x - 0.5);
                float angel = 0.0;

                if ((this_p.x != 0.0) && (this_p.y != 0.0))
                    angel = atan(this_p.y, this_p.x);
                else
                    angel = 0.0;

                bool isInside = false;

                if ((angel >= radians(-180.0)) && (angel < offset_degrees[1]))
                    isInside = PointinTriangle(real_values[0], real_values[1], const_origin, this_p);
                else if((angel >= offset_degrees[1]) && (angel < offset_degrees[2]))
                    isInside = PointinTriangle(real_values[1], real_values[2], const_origin, this_p);
                else if((angel >= offset_degrees[2]) && (angel < offset_degrees[3]))
                    isInside = PointinTriangle(real_values[2], real_values[3], const_origin, this_p);
                else if((angel >= offset_degrees[3]) && (angel < offset_degrees[4]))
                    isInside = PointinTriangle(real_values[3], real_values[4], const_origin, this_p);
                else if((angel >= offset_degrees[4]) && (angel < offset_degrees[0]))
                    isInside = PointinTriangle(real_values[4], real_values[0], const_origin, this_p);
                    
                // 返回颜色值
                if( !isInside )
                {
                    //vec4 color = texture2D( CC_Texture0, v_texCoord );
                    //color.w = 0.0;
                    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
                }
                else
                {
                    vec4 color = v_fragmentColor;  // * texture2D( CC_Texture0, v_texCoord ); 
                    gl_FragColor = color * (1.0 + length(this_p) / const_radarR);
                }
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_whitening',
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;

            void main()
            {
                vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                gl_FragColor.rgba = vec4( color.a, color.a, color.a, color.a );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_scanner_light',              -- 扫光
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[

            varying vec2 v_texCoord;
            varying vec4 v_fragmentColor;
            varying vec4 v_fragmentColor2;
            uniform sampler2D CC_Texture0;
            uniform sampler2D CC_Texture1;

            uniform vec4 CC_Custom;
            uniform vec4 CC_Custom_Ex;

            void main()
            {
                vec2 this_p = v_texCoord;
                //vec4 color = texture2D( CC_Texture1, v_fragmentColor2.xy);
                gl_FragColor = texture2D( CC_Texture0, v_texCoord );

                float x1 = CC_Custom.x;
                float y1 = CC_Custom.y;
                float k1 = CC_Custom.z;
                float b1 = CC_Custom.w;

                float x2 = CC_Custom_Ex.x;
                float y2 = CC_Custom_Ex.y;
                float k2 = CC_Custom_Ex.z;
                float b2 = CC_Custom_Ex.w;

                float limit1 = k1 * (this_p.x - x1) + b1 + y1;
                float limit2 = k2 * (this_p.x - x2) + b2 + y2;

                if ((min(limit1, limit2) < this_p.y) && (this_p.y < max(limit1, limit2)) && (gl_FragColor.r + gl_FragColor.g + gl_FragColor.b > 0.3))
                    {
                        //gl_FragColor = v_fragmentColor * texture2D(CC_Texture0, v_texCoord) * vec4(color.r, color.g, color.b, 1.0);
                        //gl_FragColor = v_fragmentColor * texture2D(CC_Texture0, v_texCoord) * vec4(255.0, 255.0, 255.0, 1.0);
                        float r = 255.0 * gl_FragColor.a;
                        float g = 255.0 * gl_FragColor.a;
                        float b = 255.0 * gl_FragColor.a;
                        gl_FragColor = vec4(r, g, b, 1.0);
                        //gl_FragColor = vec4(color.r, color.g, color.b, 1.0);
                        //gl_FragColor = color;
                    }
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_avatar_sketch',              -- 彩色剪影 用于进化动画
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                gl_FragColor = texture2D( CC_Texture0, v_texCoord );
                gl_FragColor.a = gl_FragColor.a * v_fragmentColor.a;
                gl_FragColor.r = v_fragmentColor.r * gl_FragColor.a;
                gl_FragColor.g = v_fragmentColor.g * gl_FragColor.a;
                gl_FragColor.b = v_fragmentColor.b * gl_FragColor.a;

            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_equip_quality',              -- 装备品质背景
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                float dis = distance( vec2( 0.5, 1.4 ), v_texCoord );
                //float temp = dis / 0.5;
                float temp = 1.4 / dis;
                if( temp > 2.0 )
                    temp = 2.0;
                vec4 temp_color = v_fragmentColor * temp;

                gl_FragColor = temp_color * texture2D( CC_Texture0, v_texCoord );
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_lineup_quality',              -- 编队里面的品质背景
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                gl_FragColor = v_fragmentColor * texture2D( CC_Texture0, v_texCoord ) * 2.0;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_lineup_portrait_quality',              -- 编队肖像详情背景里面的品质背景
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec4 v_fragmentColor;
            varying vec2 v_texCoord;
            uniform sampler2D CC_Texture0;
            uniform vec4 CC_Custom;

            void main()
            {
                float min = 0.8;
                float max = 0.9;
                float temp = v_texCoord.y * ( max - min ) + min;
                //temp = ( temp - min ) / ( max - min ) + min;

                vec4 color = v_fragmentColor * temp;

                gl_FragColor = color * texture2D( CC_Texture0, v_texCoord ) * 3.3;
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
    {
        shader_name = 'position_texture_color_mall_good_count_down',            --商店的倒计时
        vertex_shader = vertex_shader_position_texture_color,
        fragment_shader = [[

            varying vec2 v_texCoord;
            varying vec4 v_fragmentColor;
            varying vec4 v_fragmentColor2;
            uniform sampler2D CC_Texture0;

            uniform vec4 CC_Custom;

            void main()
            {
                vec2 this_p = v_texCoord;
                gl_FragColor = texture2D( CC_Texture0, v_texCoord );

                float pi = radians(180.0);
                float p  = CC_Custom.x * pi * 2.0;
                float offset = 0.5;

                float x_trans = this_p.x - 0.5;
                float y_trans = 0.5 - this_p.y;

                float angle;

                if ( ( y_trans > 0.0 ) && ( x_trans > 0.0 ) ) 
                {
                    angle = atan( y_trans / x_trans );
                }
                if ( ( y_trans > 0.0 ) && ( x_trans < 0.0 ) ) 
                {
                    angle = atan( -x_trans / y_trans ) + 0.5 * pi;
                }
                if ( ( y_trans < 0.0 ) && ( x_trans < 0.0 ) ) 
                {
                    angle = atan( y_trans / x_trans ) + 1.0 * pi;
                }
                if ( ( y_trans < 0.0 ) && ( x_trans > 0.0 ) ) 
                {
                    angle = atan( -x_trans / y_trans ) + 1.5 * pi;
                }

                angle = angle - offset * pi;
                if ( angle < 0.0 )
                    angle = 2.0 * pi + angle;

                if ( angle < p )
                {
                    gl_FragColor = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                }
                else
                {
                    gl_FragColor = vec4( 0.0, 0.0, 0.0, 0.0 );
                }

                //if ( ( angle < p ) && ( gl_FragColor.r + gl_FragColor.g + gl_FragColor.b > 0.3 ) )
                //{
                //    vec4 color = v_fragmentColor * texture2D( CC_Texture0, v_texCoord );
                //    float f = 0.3 * color.r + 0.6 * color.g + 0.1 * color.b;
                //    gl_FragColor = vec4( f, f, f, color.a );
                //}
                //else
                //{
                //    gl_FragColor = vec4( 0.0, 0.0, 0.0, 0.0 );
                //}
            }
        ]],
        attributes = {
            { name = 'a_position', index = kCCVertexAttrib_Position, },
            { name = 'a_color', index = kCCVertexAttrib_Color, },
            { name = 'a_texCoord', index = kCCVertexAttrib_TexCoords, },
        },
    },
}

function initCustomShaders()
    for _,shader_info in pairs( custom_shaders ) do
        local p = CCGLProgram:create()
        p:initWithVertexShaderByteArray( shader_info.vertex_shader, shader_info.fragment_shader )

        for _,attrib in ipairs( shader_info.attributes ) do
            p:addAttribute( attrib.name, attrib.index )
        end

        p:link()
        p:updateUniforms()

        CCShaderCache:sharedShaderCache():addProgram( p, shader_info.shader_name )
    end
end

function reloadOpenGL()
    for _,shader_info in pairs( custom_shaders ) do
        local p = CCShaderCache:sharedShaderCache():programForKey( shader_info.shader_name )
        if p ~= nil then
            p:reset()
            p:initWithVertexShaderByteArray( shader_info.vertex_shader, shader_info.fragment_shader )

            for _,attrib in ipairs( shader_info.attributes ) do
                p:addAttribute( attrib.name, attrib.index )
            end

            p:link()
            p:updateUniforms()
        end
    end

    TLFontTex:sharedTLFontTex():initFontTexture( GameSettings.color_tex_file, GameSettings.color_tex_row, GameSettings.color_tex_col, GameSettings.color_shader )
end

CCNotificationCenter:sharedNotificationCenter():registerScriptObserver(CCDirector:sharedDirector():getRunningScene(), reloadOpenGL, "event_come_to_foreground")
