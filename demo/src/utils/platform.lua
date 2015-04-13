local luaj = require 'utils.luaj'

PlatformBase = class('platform_base')

-- 初始化，注册回调
function PlatformBase:ctor()
    register_platform_callback(CB_LOGIN_SUCCESS, function(json)
        local u = cjson.decode(json)
        CCLuaLog(string.format('token:%s, userID:%s', u.token, u.userID))
        CCLuaLog("lua onLoginSuccess:" .. json);
    end)
    register_platform_callback(CB_LOGIN_FAIL, function(json)
        CCLuaLog("lua onLoginFail");
    end)
    register_platform_callback(CB_DONT_SUPPORT_LOGIN, function(json)
        CCLuaLog("lua don't support login");
    end)

    register_platform_callback(CB_LOGOUT_SUCCESS, function(json)
        CCLuaLog("lua onLogoutSuccess");
    end)

    register_platform_callback(CB_PAY_SUCCESS, function(json)
        CCLuaLog("lua onPaySuccess");
    end)
    register_platform_callback(CB_PAY_FAIL, function(json)
        CCLuaLog("lua onPayFail");
    end)

    register_platform_callback(CB_EXIT_CUSTOM, function(json)
        CCLuaLog("lua on custom exit");
    end)
end

-- 登录
function PlatformBase:login()
    luaj.callStaticMethod("org/yunyue/poem", "accountLogin", {}, '()V')
end

-- 主动登出
function PlatformBase:logout()
    luaj.callStaticMethod("org/yunyue/poem", "accountLogout", {}, '()V')
end

-- 切换帐号
function PlatformBase:switch()
    luaj.callStaticMethod("org/yunyue/poem", "accountSwitch", {}, '()V')
end

-- roleInfo：构成
--      id          角色ID
--      name        角色名
--      faction     工会名
--      vip         vip等级
--      level       角色等级
--      serverID    所在服务器ID
-- }
--
function PlatformBase:pay(goodID, goodName, goodPrice, roleInfo)
    local sig = '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FLjava/lang/String;Ljava/lang/String;)V'
    local callBackInfo = string.format('%d_%d', roleInfo.serverID, roleInfo.id)
    local args = {goodID, goodName, "test-order-id2", goodPrice, callBackInfo, cjson.encode(roleInfo)}
    luaj.callStaticMethod("org/yunyue/poem", "pay", args, sig)
end
