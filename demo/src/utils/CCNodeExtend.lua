--./cocos2dx/CCNodeExtend.lua
require 'utils.class'

NODE_PRO_NULL		    = 0    -- 空属性, 用于延时
NODE_PRO_X			    = 1		-- x轴位置
NODE_PRO_Y			    = 2		-- y轴位置
NODE_PRO_Z			    = 3
NODE_PRO_ROTATION	    = 4		-- 旋转
NODE_PRO_SCALE		    = 5		-- 等比缩放
NODE_PRO_SCALEX		    = 6		-- x轴缩放
NODE_PRO_SCALEY		    = 7		-- y轴缩放
NODE_PRO_SKEWX		    = 8		-- x轴斜向
NODE_PRO_SKEWY		    = 9		-- y轴斜向
NODE_PRO_ISVISIBLE	    = 10		-- 显示
NODE_PRO_ALPHA		    = 11	-- 半透明
NODE_PRO_RED			= 12	-- 红
NODE_PRO_GREEN		    = 13	-- 绿
NODE_PRO_BLUE		    = 14	-- 蓝
NODE_PRO_SHADER_CUSTOM1 = 15	-- shader自定义数据
NODE_PRO_SHADER_CUSTOM2 = 16 -- shader自定义数据
NODE_PRO_SHADER_CUSTOM3 = 17 -- shader自定义数据
NODE_PRO_SHADER_CUSTOM4 = 18 -- shader自定义数据
NODE_PRO_PARTICLE_X     = 19 -- 粒子
NODE_PRO_PARTICLE_Y     = 20 -- 粒子
NODE_PRO_CUSTOM         = 21 -- 粒子


local winSize = CCDirector:sharedDirector():getWinSize();

CCNodeExtend = class( 'CCNodeExtend' )
CCNodeExtend.__index = CCNodeExtend

function CCNodeExtend.extend( target )
    local t = tolua.getpeer( target )
    if t and t.is_valid then
        return target
    end

    t = { is_valid = true }
    tolua.setpeer( target, t )

    setmetatable( t, CCNodeExtend )

    -- 
    target.init_exit_flag = nil
    target.exit_flag = nil

    target.icon = nil

    return target
end

-- gray ----------------------------------------------------------------------------------------------------------------------------------------------------------------
function CCNodeExtend:setGray( bIsGray )
    setNodeColor( self, bIsGray and ccc3( 85, 85, 85 ) or ccc3( 255, 255, 255 ) )
end

function CCNodeExtend:outerGlow( flag, r, g, b, param )
    local shader_name = flag and 'position_texture_color_outer_glow' or 'ShaderPositionTextureColor'
    local shader_program = CCShaderCache:sharedShaderCache():programForKey( shader_name )
    if flag then self:setCustomUniforms( r, g, b, param ) end
    setNodeShaderProgram( self, shader_program )
end

-- icon
function CCNodeExtend:loadIcon( res_id, icon_type, icSize, sprite_shader_name )
    -- 清理一下
    self:removeIcon()

    -- res_id 是否合理
    if not res_id or res_id <= 0 then return end

    -- icon
    self.icon = loadIcon( res_id, icon_type or 'icon', sprite_shader_name )
    if self.icon then self:addChild( self.icon ) end
end

function CCNodeExtend:removeIcon()
    if self.icon then
        self.icon:removeFromParentAndCleanup( true )
        self.icon = nil
    end
end

-- tween start ---------------------------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
function CCNodeExtend:removeTween( handle )
    self.all_tween_handles[handle] = nil
    TLTweenManager:sharedTLTweenManager():removeTween( handle )
end

function CCNodeExtend:removeAllTween()
    for handle,_ in pairs( self.all_tween_handles or {} ) do
        TLTweenManager:sharedTLTweenManager():removeTween( handle )
    end
    self.all_tween_handles = {}
end

-- ease_id ：算法的类型，如 LINEAR_IN，BOUNCE_IN 等
-- property ：目标属性类型，如 NODE_PRO_X，NODE_PRO_Y 等
-- delay ：延时
-- duration ：持续时间
-- interval ：每次循环的间隔时间
-- from ：属性起始值
-- to ：数值结束值
-- loop_count ：循环次数 （ -1 无限循环，0 或者 1 都为播放 1 次 ）
-- call_back_close ：结束时的回调，型如：function( tween_id, loop ) end ，其中 tween_id 为当前的 tween 句柄，loop 代表循环剩余次数，值为 0 时，表示该 tween 将会被删除
-- return ：返回动画句柄，通过该句柄，可以调用 RemoveTween 删除该动画

-- from to ----------------------------------------------------------------------------------------------------------------------------------------------------------
function CCNodeExtend:tweenFromToOnce( ease_id, property, delay, duration, from, to, call_back_close, set_pro_func)
    return self:tweenFromTo( ease_id, property, delay, duration, 0, from, to, 0, call_back_close, set_pro_func )
end

function CCNodeExtend:tweenFromToEx( ease_id, property, delay, duration, from, to, loop_count, call_back_close, set_pro_func)
    return self:tweenFromTo( ease_id, property, delay, duration, 0, from, to, loop_count, call_back_close, set_pro_func )
end

function CCNodeExtend:tweenFromTo( ease_id, property, delay, duration, interval, from, to, loop_count, call_back_close, set_pro_func )
    local node_property_type = {
        [NODE_PRO_NULL]           = function( value ) end,
        [NODE_PRO_X]              = function( value ) self:setPositionX( value ) end,
        [NODE_PRO_Y]              = function( value ) self:setPositionY( value ) end,
        [NODE_PRO_Z]              = function( value ) self:setZOrder( value ) end,
        [NODE_PRO_ROTATION]       = function( value ) self:setRotation( value ) end,
        [NODE_PRO_SCALE]          = function( value ) self:setScale( value ) end,
        [NODE_PRO_SCALEX]         = function( value ) self:setScaleX( value ) end,
        [NODE_PRO_SCALEY]         = function( value ) self:setScaleY( value ) end,
        [NODE_PRO_SKEWX]          = function( value ) self:setSkewX( value ) end,
        [NODE_PRO_SKEWY]          = function( value ) self:setSkewY( value ) end,
        [NODE_PRO_ISVISIBLE]      = function( value ) self:setVisible( value ~= 0 and true or false ) end,
        [NODE_PRO_ALPHA]          = function( value ) setNodeAlphaLua( self, value ) end,
        [NODE_PRO_RED]            = function( value ) setNodeColorRLua( self, value ) end,
        [NODE_PRO_GREEN]          = function( value ) setNodeColorGLua( self, value ) end,
        [NODE_PRO_BLUE]           = function( value ) setNodeColorBLua( self, value ) end,
        [NODE_PRO_SHADER_CUSTOM1] = function( value ) toSprite( self ):setCustomUniforms1( value ) end,
        [NODE_PRO_SHADER_CUSTOM2] = function( value ) toSprite( self ):setCustomUniforms2( value ) end,
        [NODE_PRO_SHADER_CUSTOM3] = function( value ) toSprite( self ):setCustomUniforms3( value ) end,
        [NODE_PRO_SHADER_CUSTOM4] = function( value ) toSprite( self ):setCustomUniforms4( value ) end,
        [NODE_PRO_PARTICLE_X]     = function( value ) self:SetPositionX( value ) end,
        [NODE_PRO_PARTICLE_Y]     = function( value ) self:SetPositionY( value ) end,
        [NODE_PRO_CUSTOM]         = function( value ) set_pro_func( value ) end,
    }

    local set_property_func = node_property_type[property]
    if not set_property_func then
        return CCLuaLog( debug.traceback() )
    end

    if self.exit_flag then
        CCLuaLog( 'self.exit_flag = true' )
        CCLuaLog( debug.traceback() )
        return -1
    end

    if not self.init_exit_flag then
        self.init_exit_flag = true

        self.all_tween_handles = {}

        self:registerScriptHandler( function( evt )
            if evt == 'exit' then
                self:removeAllTween()

                self.is_valid = false
                self.exit_flag = true
            end
        end)
    end

    -- 
    call_back_close = call_back_close or function() end
    local handle = TLTweenManager:sharedTLTweenManager():tweenFromTo( ease_id, delay, duration, interval, from, to, loop_count, set_property_func, call_back_close )

    self.all_tween_handles[handle] = 1

    return handle
end

------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- tween end -----------------------------------------------------------------------------------------------------------------------------------------------------------


