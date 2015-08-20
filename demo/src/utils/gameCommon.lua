-- ./utils/gameCommon.lua
G_LOCK = false



--===================MUSIC PART========================
GAME_MUSIC_INFO = {
    --UNIVERSE
    LOGIN                = 'music/jingle_title.mp3',
    NORMAL_BACKGROUND    = 'music/BM_Field_Master.mp3',

    POSITIVE_BTN_ONCLICK = 'music/bgm_button.mp3',
    NEGATIVE_BTN_ONCLICK = 'music/se_cancel.mp3',
    
    --FB
    
    --FIGHT

    --FIGHT BALANCE

    --HERO MODULE
    HERO_ICON_WIDGET_LONG_PRESS = 'music/bgm_button.mp3',

    --HERO BREED
    HERO_BREED_RESULT_START  = 'music/se_monster_fusion_start.mp3',
    HERO_BREED_RESULT_FINAL  = 'music/se_monster_fusion_end.mp3',
    HERO_BREED_RESULT_normal = 'music/se_fusion_result_normal.mp3',
    HERO_BREED_RESULT_super  = 'music/se_fusion_result_success.mp3',
    HERO_BREED_RESULT_ultra  = 'music/se_fusion_result_bigsuccess.mp3',
    HERO_BREED_RESULT_LEVELUP = 'music/se_levelup.mp3',

    --HERO SHOW
    HERO_SHOW_1 = 'music/se_gacha_charge.mp3',
    HERO_SHOW_2 = 'music/se_gacha_spawn.mp3',
    HERO_SHOW_3 = 'music/se_gacha_photon.mp3',

    --HERO_RARITY
    HERO_RARITY_1 = 'music/jingle_get_rare.mp3',
    HERO_RARITY_2 = 'music/jingle_get_rare.mp3',
    HERO_RARITY_3 = 'music/jingle_get_super_rare.mp3',
    HERO_RARITY_4 = 'music/jingle_get_super_rare.mp3',
    HERO_RARITY_5 = 'music/jingle_get_ultra_super_rare.mp3',
    HERO_RARITY_6 = 'music/jingle_get_ultra_super_rare.mp3',
    HERO_RARITY_7 = 'music/jingle_get_ultra_super_rare.mp3',
    HERO_RARITY_8 = 'music/jingle_get_ultra_super_rare.mp3',
}


--游戏模块枚举
GAME_SYSTEM_INFO = {
    --推图与战斗
    WORLD_MENU = {
        main_menu_index = 1,
        name = 'WORLD_MENU',
        --titlePNG = 'word_a001.png',
    },

    WORLD_ELITE  = {
        name = 'WORLD_ACTIVE',     --精英本
        index = 1,
        --titlePNG = '',
        access = 1000009,          --开启条件
        maxcnt = 2,                --每天挑战上限
    },

    WORLD_FB = {
        name = 'WORLD_FB',
        index = 2,
        --titlePNG = '',
    },

    WORLD_ACTIVE = {
        name = 'WORLD_ACTIVE',     --活动本
        index = 3,
        --titlePNG = '',
        access = 1000009,          --开启条件
        
    },


    FIGHT = {
        name = 'FIGHT',            --战斗
        titlePNG = '',
    },

    FIGHT_BALANCE     = {
        name = 'FIGHT_BALANCE',    --战斗结算
        titlePNG = '',
    },

    --英雄
    HERO_MENU = {
        main_menu_index = 2,
        name = 'HERO_MENU',
        titlePNG = 'word_a003.png',
    },

    HERO_TEAM = {
        menuIndex = 1,
        name = 'HERO_TEAM',        --英雄编队
        titlePNG = 'word_a008.png',
    },

    HERO_BREED        = {
        menuIndex = 2,
        name = 'HERO_BREED',       --英雄育成
        titlePNG = 'word_a009.png',
    },

    HERO_EVOLUTION    = {
        menuIndex = 3,
        name = 'HERO_EVOLUTION',   --英雄进化
        titlePNG = 'word_a010.png',
    },

    --神将兑换
    HERO_PATCH_CHANGE = {
        menuIndex = 4,
        name = 'HERO_PATCH_CHANGE',
        titlePNG = 'word_a042.png',
    },

    HERO_BREAK_THROUGH = {
        menuIndex = 5,
        name = 'HERO_BREAK_THROUGH',
        titlePNG = 'ui_0054_5.png',
    },

    HERO_BOX          = {
        menuIndex = 6,
        name = 'HERO_BOX',         --英雄盒子
        titlePNG = 'word_a012.png',
    },

    HERO_SALE         = {
        menuIndex = 7,
        name = 'HERO_SALE',        --英雄出售
        titlePNG = 'word_a013.png',
    },

    MALL_MENU         = {
        main_menu_index = 3,
        name = 'MALL_MENU',
        titlePNG = 'word_a004.png',
    },

    --[[碎片系统
    HERO_PATCH_SYSTEM_MENU     = {
        main_menu_index = 3,
        name = 'HERO_PATCH_SYSTEM_MENU',
        titlePNG = 'word_a039.png',
    },
    --]]

    -- 银币召唤
    MALL_SUMMON_HERO_MONEY = {
        menuIndex = 1,
        name = 'MALL_SUMMON_HERO_MONEY',
    },

    -- 银币召唤 单次
    MALL_SUMMON_HERO_MONEY_SINGLE = {
        menuIndex = 103,
        name = 'MALL_SUMMON_HERO_MONEY_SINGLE',
    },

    -- 银币召唤 多次
    MALL_SUMMON_HERO_MONEY_MULTI  = {
        menuIndex = 104,
        name = 'MALL_SUMMON_HERO_MONEY_MULTI',
    },

    -- 金币召唤
    MALL_SUMMON_HERO_GOLD         = {
        menuIndex = 2,
        name = 'MALL_SUMMON_HERO_GOLD',
    },

    -- 金币召唤 单次
    MALL_SUMMON_HERO_GOLD_SINGLE  = {
        menuIndex = 105,
        name = 'MALL_SUMMON_HERO_GOLD_SINGLE',
    },

    -- 金币召唤 多次
    MALL_SUMMON_HERO_GOLD_MULTI   = {
        menuIndex = 106,
        name = 'MALL_SUMMON_HERO_GOLD_MULTI',
    },

    SELECT_HERO         = {
        menuIndex = 3,
        name = 'SELECT_HERO',
    },

    -- 临时金角商店
    TEMP_GOLDEN_SHOP              = {
        menuIndex = 4,
        name = 'TEMP_GOLDEN_SHOP',
    },

    -- 临时银角商店
    TEMP_SILVER_SHOP              = {
        menuIndex = 5,
        name = 'TEMP_SILVER_SHOP',
    },

    -- 永久开启金角商店
    OPEN_GOLDEN_SHOP             = {
        menuIndex = 6,
        name = 'OPEN_GOLDEN_SHOP',
    },

    -- 永久开启银角商店
    OPEN_SILVER_SHOP             = {
        menuIndex = 7,
        name = 'OPEN_SILVER_SHOP',
    },

    -- 金角商店
    GOLDEN_SHOP         = {
        menuIndex = 8,
        name = 'GOLDEN_SHOP',
    },

    -- 银角商店
    SILVER_SHOP         = {
        menuIndex = 9,
        name = 'SILVER_SHOP',
    },

    -- 天庭商店
    SKY_SHOP            = {
        menuIndex = 10,
        name = 'SKY_SHOP',
    },

    --[[ 扩充背包
    MALL_EXPEND_BOX     = {
        menuIndex = 8,
        name = 'MALL_EXPEND_BOX'
    },

    -- 炼银
    GOLD_TO_MONEY     = {
        menuIndex = 9,
        name = 'GOLD_TO_MONEY'
    },

    SUMMON_MENU       = {
        main_menu_index = 4,
        name = 'SUMMON_MENU',      --抽将
        titlePNG = 'word_a002.png',
    },

    SUMMON_HERO_GP    = {
        menuIndex = 1,
        name = 'SUMMON_HERO_GP',
    },

    SUMMON_HERO_GOLD_SINGLE = {
        menuIndex = 2,
        name = 'SUMMON_HERO_GOLD_SINGLE',
    },

    SUMMON_HERO_GOLD_MULTI = {
        menuIndex = 3,
        name = 'SUMMON_HERO_GOLD_MULTI',
    },
    --]]

    --PVP
    PVP_MENU          = {
        main_menu_index = 5,
        name = 'PVP_MENU',              --PVP
        titlePNG = 'word_a006.png',
    },

    PVP_OPPONENT_LIST = {
        name = 'PVP_OPPONENT_LIST', --PVP对手列表
        titlePNG = 'word_a015.png',
    },

    PVP_RANK_LIST = {
        name = 'PVP_RANK_LIST',     --PVP排行榜列表
        titlePNG = 'word_a020.png',
    },

    PVP_REWARD_LIST = {
        name = 'PVP_REWARD_LIST',   --PVP奖励列表
        titlePNG = 'word_a021.png',
    },

    PVP_RECORDS = {
        name = 'PVP_RECORDS',       --PVP对战记录
        titlePNG = 'word_a043.png',
    },

    --公会
    FACTION_MENU      = {
        main_menu_index = 6,
        name = 'FACTION_MENU',          --公会
        titlePNG = 'word_a005.png',
    },

    FACTION_SEARCH    = {
        name = 'FACTION_MENU',          --公会搜索菜单
        titlePNG = 'word_n043.png',
    },

    FACTION_SEARCH_RESULT = {
        name = 'FACTION_SEARCH_RESULT',
        titlePNG = 'word_a026.png',
    },

    FACTION_INVITE = {
        name = 'FACTION_INVITE',
        titlePNG = 'word_a031.png',
    },

    FACTION_ENHANCE = {
        name = 'FACTION_ENHANCE', 
        titlePNG = 'ui_0053_2.png',
    },

    FACTION_ENHANCE_ATK = {
        name = 'FACTION_ENHANCE_ATK',
        titlePNG = 'word_a023.png',
    },

    FACTION_ENHANCE_HP = {
        name = 'FACTION_ENHANCE_HP', 
        titlePNG = 'word_a024.png',
    },

    FACTION_ENHANCE_CRIT = {
        name = 'FACTION_ENHANCE_CRIT',
        titlePNG = 'word_a025.png',
    },

    FACTION_MEMBERS = {
        name = 'FACTION_MEMBERS',
        titlePNG = 'word_a032.png',
    },

    FACTION_MANAGE = {
        name = 'FACTION_MANAGE',
        titlePNG = 'word_a033.png',
    },

    FACTION_MANAGE_NEWCHAIRMAN = {
        name = 'FACTION_MANAGE_NEWCHAIRMAN',
        titlePNG = 'word_a035.png',
    },

    FACTION_MANAGE_KICKOUT = {
        name = 'FACTION_MANAGE_KICKOUT',
        titlePNG = 'word_a034.png',
    },

    FACITON_MANAGE_DISMISS = {
        name = 'FACTION_MANAGE_DISMISS',
        titlePNG = 'word_a037.png',
    },

    FACTION_MANAGE_QUIT = {
        name = 'FACTION_MANAGE_QUIT',
        titlePNG = 'word_a038.png',
    },

    FACTION_RANK_LIST = {
        name = 'FACTION_MENU',          --公会排行榜
        titlePNG = 'word_a028.png',
    },

    FACTION_UPGRADE = {
        name = 'FACTION_MENU',
        titlePNG = 'word_a027.png',
    },

    --[[
    FACTION_REWARD_LIST = {
        name = 'FACTION_MENU',          --公会奖励
        titlePNG = 'word_a027.png',
    },
    --]]

    --设置与其他
    SETTINGS_MENU          = {
        main_menu_index = 7,
        name = 'SETTINGS_MENU',         --设置
        titlePNG = 'word_a007.png',
    },

    ACHIEVEMENT       = {
        name = 'ACHIEVEMENT',      --成就
        titlePNG = 'word_a019.png',
    },

    MUSIC_CONTROL     = {
        name = 'MUSIC_CONTROL',
        titlePNG = '',
    },

    HERO_GALLERY = {      --图鉴
        name = 'HERO_GALLERY',
        titlePNG = 'word_a018.png',
    },

    --邮箱
    MAIL              = {
        name = 'MAIL',
        titlePNG = 'word_a017.png',
    },

    --返回按钮
    BACK              = {
        main_menu_index = 8,
        name = 'BACK',
    },

    --[[
    HERO_PATCH = {
        menuIndex = 3,
        name = 'HERO_PATCH',        --英雄编队
        titlePNG = 'word_n061.png',
    },

    HERO_PATCH = {
        menuIndex = 4,
        name = 'HERO_PATCH',        --英雄编队
        titlePNG = 'word_n062.png',
    },
    --]]

}



---- 一个关卡支持配置可能掉落最大数量
--MAX_LEVEL_REWARDS_COUNT = 4

-- 一个副本里面，最大怪物组的数量
max_monster_group = 5

-- 一个怪物组里面，最大的怪物数量
max_monster_count = 4



--DELETE
hero_attr_fire      = 1
hero_attr_water     = 2
hero_attr_wood      = 3
hero_attr_light     = 4
hero_attr_dark      = 5
hero_attr_null      = 6
hero_attr_pvp       = 7


if ccc4 == nil then
    function ccc4()
    end
end

--英雄属性枚举
ATTR_NAME = {
    [hero_attr_fire] = {
        name = _YYTEXT('火'),
        icon = '',
        hp_icon = '60001_p2_1.png',                                 -- 
        skill_attack_sound = 'music/se_skill_fire.mp3',                   -- 技能攻击使用的音效
        skill_attr = 1,
        play_effect_name = { '60315/60315_1', '60315/60315_2', 'music/skill_fire0.mp3' },    -- 起手光效，后面的是腾空时候的持续重复光效
        effect_color = ccc4( 255, 60, 0, 255 ),                     -- 受击光效的颜色叠加
        special_effect_name = {                                     -- 施法持续的 AOE 光效或者是具体格子的表现光效
            ['ALL'] = { '60318/60318_1', 0.5, 'music/skill_fire4.mp3' },                  -- 
            ['LINE'] = { '60316/60316_1', 0.5, 'music/skill_fire3.mp3' },
            ['BOX'] = { '60317/60317_1', 1.3, 'music/skill_fire2.mp3' },
            ['POINT'] = { '60333/60333_1', 0.8, 'music/skill_fire1.mp3', },
        },
        special_effect_name_2 = '60318/60318_2',        -- ALL 时候的残留光效
    },
    [hero_attr_water] = {
        name = _YYTEXT('水'),
        icon = '',
        hp_icon = '60001_p2_2.png',
        skill_attack_sound = 'music/se_skill_water.mp3',
        skill_attr = 2,
        play_effect_name = { '60310/60310_1', '60310/60310_2', 'music/skill_water0.mp3', },
        effect_color = ccc4( 0, 174, 255, 255 ),
        special_effect_name = {
            ['ALL'] = { '60314/60314_1', 0.7, 'music/skill_water4.mp3', },
            ['LINE'] = { '60312/60312_1', 0.5, 'music/skill_water3.mp3', },
            ['BOX'] = { '60313/60313_1', 1.5, 'music/skill_water2.mp3', },
            ['POINT'] = { '60332/60332_1', 0.8, 'music/skill_water1.mp3', }
        },
        special_effect_name_2 = '60314/60314_2',
    },
    [hero_attr_wood] = {
        name = _YYTEXT('木'),
        icon = '',
        hp_icon = '60001_p2_3.png',
        skill_attack_sound = 'music/se_skill_wood.mp3',
        skill_attr = 4,
        play_effect_name = { '60319/60319_1', '60319/60319_2', 'music/skill_wood0.mp3' },
        effect_color = ccc4( 0, 255, 36, 255 ),
        special_effect_name = {
            ['ALL'] = { '60322/60322_1', 1.5, 'music/skill_wood4.mp3' },
            ['LINE'] = { '60320/60320_1', 0.6, 'music/skill_wood3.mp3' },
            ['BOX'] = { '60321/60321_1', 0.5, 'music/skill_wood2.mp3' },
            ['POINT'] = { '60334/60334_1', 0.8, 'music/skill_wood1.mp3' },
        },
        special_effect_name_2 = '60322/60322_2',
    },
    [hero_attr_light] = {
        name = _YYTEXT('光'),
        icon = '',
        hp_icon = '60001_p2_5.png',
        skill_attack_sound = 'music/se_skill_light.mp3',
        skill_attr = 8,
        play_effect_name = { '60300/60300_1', '60300/60300_2', 'music/skill_light0.mp3' },
        effect_color = ccc4( 252, 255, 0, 255 ),
        special_effect_name = {
            ['ALL'] = { '60304/60304_1', 1.2, 'music/skill_light4.mp3' },
            ['LINE'] = { '60302/60302_1', 0.5, 'music/skill_light3.mp3' },
            ['BOX'] = { '60303/60303_1', 0.5, 'music/skill_light2.mp3' },
            ['POINT'] = { '60330/60330_1', 0.8, 'music/skill_light1.mp3' },
        },
        special_effect_name_2 = '60304/60304_2',
    },
    [hero_attr_dark] = {
        name = _YYTEXT('暗'),
        icon = '',
        hp_icon = '60001_p2_4.png',
        skill_attack_sound = 'music/se_skill_dark.mp3',
        skill_attr = 16,
        play_effect_name = { '60305/60305_1', '60305/60305_2', 'music/skill_dark0.mp3' },
        effect_color = ccc4( 168, 0, 255, 255 ),
        special_effect_name = {
            ['ALL'] = { '60309/60309_1', 1.7, 'music/skill_dark4.mp3' },
            ['LINE'] = { '60307/60307_1', 0.4, 'music/skill_dark3.mp3' },
            ['BOX'] = { '60308/60308_1', 0.8, 'music/skill_dark2.mp3' },
            ['POINT'] = { '60331/60331_1', 0.8, 'music/skill_dark1.mp3' },
        },
        special_effect_name_2 = '60309/60309_2',
    },
}



-------------------------------------------------------------------------------------------------------------------------------------
-- skill start ----------------------------------------------------------------------------------------------------------------------
-- 技能的类型，1 表示普通，0 表示连锁，对应技能表里面的 "type" 字段
skill_type = {
    CHAIN       = 0,
    NORMAL      = 1,
}

-- 技能的范围，对应技能表里面的 "rge" 字段
skill_range = {
    SELF = 0x00000001,   -- 自己            1
    TL   = 0x00000002,   -- 左上            2
    T    = 0x00000004,   -- 上              4
    TR   = 0x00000008,   -- 右上            8
    L    = 0x00000010,   -- 左              16
    R    = 0x00000020,   -- 右              32
    BL   = 0x00000040,   -- 左下            64
    B    = 0x00000080,   -- 下              128
    BR   = 0x00000100,   -- 右下            256
    V    = 0x00000200,   -- 纵一列          512
    H    = 0x00000400,   -- 横一列          1024
    CL   = 0x00000800,   -- 左斜            2048
    CR   = 0x00001000,   -- 右斜            4096
    ALL  = 0x00002000,   -- 全体            8192
}

-- 技能的性质，对应技能表里面的 "kind" 字段
skill_kind = {
    ATTACK      = 0,
    RESTORE     = 1,
    BUFF        = 2,
    DEBUFF      = 3,
}

-- buff 
skill_buf = {
    ['AtkUp']           = { val = 0x00000001, def_val1 = 0.0, def_val2 = 0.0, },           -- 攻击加成 ( 百分比 )
    ['AtkDown']         = { val = 0x00000002, def_val1 = 0.0, def_val2 = 0.0, },           -- 攻击
    ['DefUp']           = { val = 0x00000004, def_val1 = 0.0, def_val2 = 0.0, },           -- 防御
    ['DefDown']         = { val = 0x00000008, def_val1 = 0.0, def_val2 = 0.0, },           -- 防御
    ['CriticalUp']      = { val = 0x00000010, def_val1 = 0.0, def_val2 = 0.0, },           -- 暴击概率
    ['Num']             = { val = 0x00000011, def_val1 = 0.0, def_val2 = 0.0, },           --
    ['CriticalDown']    = { val = 0x00000020, def_val1 = 0.0, def_val2 = 0.0, },           -- 暴击
    ['Counter']         = { val = 0x00000040, def_val1 = 0.0, def_val2 = 0.0, },           -- 反击
    ['Invincible']      = { val = 0x00000080, def_val1 = 0.0, def_val2 = 0.0, },           -- 无敌 ( 闪避 )
    ['Poison']          = { val = 0x00000100, def_val1 = 0.0, def_val2 = 0.0, },           -- 中毒
    ['Paralysis']       = { val = 0x00000200, def_val1 = 0.0, def_val2 = 0.0, },           -- 麻痹
    ['RegeneA']         = { val = 0x00000400, def_val1 = 0.0, def_val2 = 0.0, },           --
    ['RegeneB']         = { val = 0x00000800, def_val1 = 0.0, def_val2 = 0.0, },           -- 
    ['DodgeUp']         = { val = 0x00001000, def_val1 = 0.0, def_val2 = 0.0, },           -- 闪避提升 ( 百分比 )
    ['DodgeDown']       = { val = 0x00002000, def_val1 = 0.0, def_val2 = 0.0, },           -- 闪避
    ['DefAttr']         = { val = 0x00004000, def_val1 = 0.0, def_val2 = 0.0, },           -- 对应的属性防御提升
    ['AddAtk']          = { val = 0x00008000, def_val1 = 0.0, def_val2 = 0.0, },           --
    ['Combo']           = { val = 0x00010000, def_val1 = 0.0, def_val2 = 0.0, },           --
    ['CriticalVal']     = { val = 0x00020000, def_val1 = 0.0, def_val2 = 0.0, },           --
    ['BonusUp']         = { val = 0x00040000, def_val1 = 0.0, def_val2 = 0.0, },           --
}

-- 显示的优先级
skill_buff_types = { 'AtkUp', 'DefUp', 'DefAttr', 'CriticalUp', 'Counter', 'Invincible', 'DodgeUp', }
skill_debuff_types = { 'AtkDown', 'DefDown', 'CriticalDown', 'DodgeDown', 'Poison', 'Paralysis', }

-- skill end ------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------


local art_number_config = {
    ['first'] = { color_index = 2, scale = 1.0, },
}

function getArtNumberColorText( num, art_type )
    local function convertToPNG( ch )
        if ch == '.' then return 'shuzi_010.png' end
        if ch == 'x' then return 'shuzi_011.png' end
        if ch == '+' then return 'shuzi_012.png' end
        if ch == '-' then return 'shuzi_013.png' end
        return string.format( 'shuzi_00%s.png', ch )
    end

    local num_info = art_number_config[art_type]
    if not num_info then return tostring( num ), 1 end

    local ret_text = string.format( '[colorindex:colorIndex=%d]', num_info.color_index )
    for _, ch in ipairs( string.toArray( tostring( num ) ) ) do
        local sprite_text = string.format( '[sprite:fileName="%s",scale=%f]', convertToPNG( ch ), num_info.scale )
        ret_text = ret_text .. sprite_text
    end

    return ret_text
end

function convertArtNumberColorText( num, scale )
    local function convertToPNG( ch )
        if ch == '.' then return 'Nshuzi_010.png' end
        if ch == 'x' then return 'Nshuzi_011.png' end
        if ch == '+' then return 'Nshuzi_012.png' end
        if ch == '-' then return 'Nshuzi_013.png' end
        if ch == '/' then return 'Nshuzi_014.png' end
        return string.format( 'Nshuzi_00%s.png', ch )
    end

    local ret_text = ''
    for _, ch in ipairs( string.toArray( tostring( num ) ) ) do
        local sprite_text = string.format( '[sprite:fileName="%s",scale=%f]', convertToPNG( ch ), scale or 1 )
        ret_text = ret_text .. sprite_text
    end

    return ret_text
end


