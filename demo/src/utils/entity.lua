--./utils/entity.lua
require "utils.enums"
require 'utils.gameCommon'
require 'utils.signal'

--全局变量
--g_verify_code          = nil
g_userID               = nil -- 当前登录用户ID
g_player               = nil -- 当前玩家
g_pets                 = {}  -- 宠物数据 pet         = hero
g_pkg_items            = {}  -- 道具背包 {[itemid] = amount}
g_equip                = {}  -- 装备背包
g_equipeds             = {}  -- 已装备列表


g_lastatklineups       = {}  -- 上次成功保存的攻击阵型
g_atklineups           = {}  -- 攻击阵型
g_lastdeflineups       = {}  -- 上次成功保存的防守阵型
g_deflineups           = {}  -- 防守阵型
g_cur_edit_lineup      = nil  -- 当前编辑阵型
g_fb_id                = nil -- 当前战斗副本id，全局唯一
g_scenes_id            = nil -- 当前战斗场景id
g_player_old_level     = nil --战斗前玩家等级
g_player_old_exp       = nil --战斗前玩家经验
g_all_mails            = {}  --全局邮件摘要信息  {[mailid] = mailinfo}
g_broadcast_infos = {
    titles = {},
    contents = {}
}
g_summon_broadcast = nil
g_pet_breed_old_info = {}
g_faction_info = {}
--g_check_explore_timestamp = nil
--g_check_faction_apply_time = nil
g_recharge_list = {}
g_vipPrivilege = {}
g_pvpRoundCheckTime = nil
g_firstSyncPlayerTimeStamp = nil


g_pets_sorttype = 3   --第一优先保存当前英雄排序方式
--全局英雄排序配置
g_pets_sortconfig = {
    first = 'LEVEL',
    second = 'ATTR',
    filter = {
        --属性技能过滤
    },
}

g_units_gallery = {
}



--格式化CD时间为短字符
function formatCDTimer2Str(cdtime)
    if cdtime <= 0 or cdtime == nil then
        return _YYTEXT(_YYTEXT('0分钟'))
    end
    local _cdtime = cdtime
    local _days = math.floor(_cdtime / 86400)
    if _days > 0 then return string.format(_YYTEXT('%d天'), _days) end
    _cdtime = _cdtime % 86400

    local _hours = math.floor(_cdtime / 3600)
    if _hours > 0 then return string.format(_YYTEXT('%d小时'), _hours) end
    _cdtime = _cdtime % 3600

    local _minutes = math.floor(_cdtime / 60)
    if _minutes > 0 then return string.format(_YYTEXT('%d分钟'), _minutes) end
    _cdtime = _cdtime % 60

    return string.format(_YYTEXT('%s秒'), _cdtime) 
end

-- 主角属性事件
function listen_me(key, callback, orderIndex)
    return signal.listen('_me_'..key, callback, orderIndex)
end
function unlisten_me(key, callback)
    return signal.unlisten('_me_'..key, callback)
end
function fire_me(key, value)
    signal.fire('_me_'..key, value)
end

g_cdtimes = {update = {}}
function listenCDAttrUpdateTime(attrName)
    listen_me(attrName,function()
        g_cdtimes.update[attrName] = os.time()
    end,100)
end

function initCDTimeListen()
    g_cdtimes = {update = {}}
    g_cdtimes = setmetatable(g_cdtimes,{__index=function(t,k)
        if t.update[k] == nil then
            return g_player[k] or 0
        else
            local dt = os.time() - t.update[k]
            return g_player[k] and (g_player[k] - dt) or 0
        end
    end,__newindex=function()CCLuaLog('don\'t change g_cdtimes directly')end})

    listenCDAttrUpdateTime( 'refresh_count_refresh_time' )
end


--解析例如'2013-08-03 12:00:00'字符串，返回秒
function parseTmStr2Sec(tmstr)
	if not tmstr then return 0 end
	if tmstr == '' then return 0 end
	local tb = splitString(tmstr, ' ')
	local tmData = {}
	local date = splitString(tb[1], '-')
	tmData.year = date[1]
	tmData.month = date[2]
	tmData.day = date[3]
	local time = splitString(tb[2], ':')	
	tmData.hour = time[1]
	tmData.min = time[2]
	tmData.sec = time[3]
	return os.time(tmData)
end

