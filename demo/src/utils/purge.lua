--./utils/purge.lua

local __purge_scene_call_back__ = {}
function addPurgeSceneCallBackFunc( call_back_func, autoremove)
    local _autoremove = autoremove or false
    __purge_scene_call_back__[call_back_func] = _autoremove
end

function removePurgeSceneCallBackFunc( call_back_func )
    __purge_scene_call_back__[call_back_func] = nil
end

function purge_scene()
    releaseCommonSHandlers()        -- 移除 common 下的所有 schedule handler
    stopAllScroller()               -- 停止所有滚动条时钟
    signal.purge()                  -- 停止所有的监听事件

    -- 移除所有的 tween 
    tweenExtend:removeAllTween()    -- 停止 tween 
    TLTweenManager:sharedTLTweenManager():removeAllTween();

    -- 执行模块的回调
    for cb, _autoremove in pairs( __purge_scene_call_back__ ) do
        cb()
        if _autoremove then
            __purge_scene_call_back__[cb] = nil
        end
    end
    --__purge_scene_call_back__ = {}

    -- 清理玩家的基本数据
    g_userID = nil
    g_player = nil
    g_pets = {}
    g_pkg_items = {}
    g_verify_code = nil 
    g_user_id = nil 
    g_sdk_username = nil 
    g_server_id = nil

    -- 清理所有的窗口
	TLWindowManager:SharedTLWindowManager():DestroyAllModuleWindow();
end

function purge_network()
    -- purge net receiver
    CNetReceiver:SharedNetReceiver():Reset()
    CNetSender:SharedNetSender():CloseSocket( NWTS_CLOSED )
end

--[[
function purge_textures()
	CCTexFontConfig:sharedTexFontConfig():EnterBackground();
    CCSpriteFrameCache:purgeSharedSpriteFrameCache()
    CCTextureCache:purgeSharedTextureCache()
end
--]]
