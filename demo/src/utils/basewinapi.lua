--++++++++++++++++++++++++++
--+    滑动菜单列表窗口    +
--++++++++++++++++++++++++++
--创建一个从右侧滑动进入退出的列表菜单窗口
--TODO 定制进入方向，界面组成
--UI Manifest
--@param:pwin父窗口
--@param:mcname元件名
--@param:winname窗口名  使用GAME_SYSTEM_INFO枚举
--@param:menuconfigs菜单项配置
--@param:scrollertype 滚动区类型: 'SINGLE', Default:MULTI
--@param:ygap
--@param:xgap
function createSlideMenuListWin(pwin, mcname, wininfo, menuconfigs, scrollertype, ygap, xgap)
    local _menuConfigs = menuconfigs

    local mc = createMovieClipWithName(mcname)
    local win = TLWindow:createWindow(mc)
    --mc:setAutoClear(false)
    
    pwin:GetNode():addChild(mc)
    pwin:AddChildWindow(win)
    win:SetWindowName(wininfo.name or 'SLIDE MENUS LIST WIN'..mcname)
    local reg = toFrame(pwin:GetNode())
    reg:setClipRegion(reg.mcBoundingBox)
    local START_POS_X =  mc.mcBoundingBox.size.width
    local scrollRgn = nil
    if scrollertype == 'SINGLE' then
        scrollRgn = scrollable(win, 'liebiao', TL_SCROLL_TYPE_UP_DOWN, ygap or 30)
    else
        scrollRgn = multi_scroller(win, 'liebiao', TL_SCROLL_TYPE_UP_DOWN, xgap or 8, ygap or 20)
    end

    local obj = {
        mc = mc,
        win = win,
        scroller = scrollRgn,
        menuItems = {},
    }
    g_historyManager.current_win = obj
    
    local lbl_title = label(win, 'zi', nil, CCImage.kAlignLeft)
    if wininfo.titlePNG ~= nil then
        lbl_title:set_rich_string(string.format("[sprite:fileName='%s']", wininfo.titlePNG))
    end
    --local mc_Title = MCLoader:sharedMCLoader():loadSpriteAsync(wininfo.titlePNG)
    --mc:getChildByName('zi'):addChild(mc_Title)
    --mc:getChildByName('zi'):setAlignment(CCImage.kAlignLeft)
    --菜单
    --local menuItems = {}
    local frame_list = toFrame( mc:getChildByName('liebiao') )
    local scroller_size = frame_list.mcBoundingBox.size
    local function setMenuList(menuconfigs)
        obj.menuItems = {}
        scrollRgn:removeall()
        --CCLuaLog('[utils/basewin]  menuconfigs count = '..tostring(table.len(menuconfigs or {})))
        for i, item_config in ipairs(menuconfigs or {}) do
            --CCLuaLog('[utils/basewin]  create menu item :'..tostring(i))
            local item_visible = true
            if item_config.visibleFunc ~= nil then item_visible = item_config.visibleFunc() end
            if item_visible then
                local item = item_config:create(scroller_size)
                item.mc:setAutoClear(false)
                local virtualmc = MCFrame:createWithBox(getBoundingBox(item.mc))
                local virtualwin = TLWindow:createWindow(virtualmc)
                local virtualobj = {
                    mc = virtualmc,
                    win = virtualwin,
                }
                virtualmc:addChild(item.mc)
                virtualwin:AddChildWindow(item.win)
                table.insert(obj.menuItems, item)
                if item_config.onClick ~= nil then
                    item.btn.onclick = function() 
                        item_config:onClick()
                    end
                end

                if item_config.availFunc ~= nil then
                    if item.btn then
                        item.btn:enable(item_config.availFunc())
                    end
                    setControlGrey(item.mc, not item_config.availFunc())
                end
                scrollRgn:append(virtualobj.win, false)
            end
        end
        scrollRgn:layout()
    end

    setMenuList(_menuConfigs)

    --未填充满屏时禁用滚动
    --if #_menuConfigs < 7 then
    --    scrollRgn:scrollEnable(false)
    --end
    
    --动画进入
    local function _open(callfunc)
        CCLuaLog('[utils/basewin]  menu anim open')
        CCLuaLog('[utils/basewin]  menuItems Count = '..tostring(table.len(obj.menuItems or {})))

        if table.len( obj.menuItems or {} ) == 0 then
            if callfunc then callfunc() end

            return
        end

        for i, item in ipairs(obj.menuItems or {}) do
            local item_config = _menuConfigs[i]
            local target = CCNodeExtend.extend(item.mc)
            item.mc:setPositionX(START_POS_X)

            if i == #obj.menuItems then
                target:tweenFromTo(LINEAR_IN, NODE_PRO_X, item_config.delay, item_config.duration, 0, START_POS_X, 0, 0, function()
                    if callfunc then callfunc() end
                end)
            else
                target:tweenFromTo(LINEAR_IN, NODE_PRO_X, item_config.delay, item_config.duration, 0, START_POS_X, 0, 0)
            end
        end
    end
    
    --带回调的动画退出
    local function _exit(callfunc)
        local doAnim = false
        --列表不为空
        for i, item in ipairs(obj.menuItems or {}) do
            doAnim = true
            local item_config = _menuConfigs[i]
            local target = CCNodeExtend.extend(item.mc)
            --if i == #menuItems then
            if i == #(obj.menuItems or {}) then
                target:tweenFromTo(LINEAR_IN, NODE_PRO_X, item_config.delay, item_config.duration, 0, 0, START_POS_X, 0, function()
                    if callfunc then callfunc() end
                end)
            else
                target:tweenFromTo(LINEAR_IN, NODE_PRO_X, item_config.delay, item_config.duration, 0, 0, START_POS_X, 0)
            end
        end

        --列表为空 直接回调
        if not doAnim and callfunc then
            callfunc()
        else
            CCLuaLog('[utils/basewin]  callfunc is nil')
        end
    end
    
    function obj:open(callfunc)
        local targettitle = CCNodeExtend.extend(lbl_title.node) --mc:getChildByName('zi'))
        targettitle:setPositionX(-200)
        targettitle:tweenFromTo(LINEAR_IN, NODE_PRO_X, 0, 0.2, 0, -200, 0, 0)
        _open(callfunc)
    end

    --FIXME 弃用
    function obj:update(menuconfig)
        --TODO: override
        if menuconfig then
            _menuConfigs = menuconfig
        end
        setMenuList(_menuConfigs)
    end

    --带进入退出动作的更新列表方法
    function obj:updateMenuConfigsWithAnim(menuconfigs, callfunc)
        if menuconfigs then
            _exit(function()
                schedule_once(function()
                    _menuConfigs = menuconfigs
                    setMenuList(_menuConfigs)
                    _open(callfunc)
                end)
            end)
        end
    end
    
    function obj:destroy(callfunc)
        --schedule_once(function()
            scrollRgn:stop()
            --menuItems = {}
            mc:removeFromParentAndCleanup(true)
            pwin:RemoveChildWindow(win)
            schedule_frames(5, removeUnusedTextures)
            win = nil
            mc = nil
            if callfunc then
                callfunc()
            end
        --end)
    end
  
    --带动画的退出方法, 不建议在destroy内调用
    function obj:exit(callfunc)
        --local targettitle = CCNodeExtend.extend(mc:getChildByName('zi'))
        local targettitle = CCNodeExtend.extend(lbl_title.node) --mc:getChildByName('zi'))
        targettitle:tweenFromTo(LINEAR_IN, NODE_PRO_X, 0, 0.2, 0, 0, -START_POS_X, 0)
        _exit(callfunc)
    end
    
    return obj
end



--++++++++++++++++++++++++++
--+      元件动画窗口      +
--++++++++++++++++++++++++++
--创建一个可定义个元件进入与退出动画的窗口
--动画支持的模式: 方向类: LEFT, RIGHT, TOP, BOTTOM,
--                尺寸类: SCALE,
--                透明度: ALPHA
--@param:mcname元件名
--@param:configs元件动作配置
--[[
configs = {
    nodename,      --required
    pattern,       --required
    DELAY,         --optional
    DURATION,      --optional
    FROM,          --optional
    TO,            --optional
--]]
--@param: set_win_callfunc
--        state: ['WIN_INIT'],
--               ['WIN_APPEND_ANIM_ELMT']
local function createBaseAnimElementWin(mcname, configs, set_win_funcs)
    local mc  = createMovieClipWithName(mcname)
    local obj = {
        mc = mc,
        elements = {},
    }

    --actions param
    local BG_WIDTH = mc.mcBoundingBox.size.width
    local BG_HEIGHT = mc.mcBoundingBox.size.height
    local DELAY = 0
    local DURATION = 0.1
    --local elements = {}

    if set_win_funcs['WIN_INIT'] ~= nil then set_win_funcs['WIN_INIT'](obj) end

    obj.elements = {}
    for i, config in ipairs(configs or {}) do
        local _node = mc:getChildByName(config.nodeName)
        local _ELMT_WIDTH = (getBoundingBox(_node)).size.width
        local _ELMT_HEIGHT = (getBoundingBox(_node)).size.height

        local PATTERN_DEFAULT = {
            LEFT   = {
                prop = NODE_PRO_X,
                from = -(BG_WIDTH + _ELMT_WIDTH) / 2,
                to   = _node:getPositionX(),
            },

            RIGHT  = {
                prop = NODE_PRO_X,
                from = (BG_WIDTH + _ELMT_WIDTH) / 2,
                to   = _node:getPositionX(),
            },

            TOP    = {
                prop = NODE_PRO_Y,
                from = (BG_HEIGHT + _ELMT_HEIGHT) / 2,
                to   = _node:getPositionY(),
            },

            BOTTOM = {
                prop = NODE_PRO_Y,
                from = -(BG_HEIGHT + _ELMT_HEIGHT) / 2,
                to   = _node:getPositionY(),
            },

            SCALE  = {
                prop = NODE_PRO_SCALE,
                from = 1,
                to   = 1,
            },

            ALPHA  = {
                prop = NODE_PRO_ALPHA,
                from = 255,
                to   = 255,
            },
        }
       
        table.insert(obj.elements, {
            node = _node,
            PROP = PATTERN_DEFAULT[config.pattern].prop,
            FROM = config.FROM or PATTERN_DEFAULT[config.pattern].from,
            TO   = config.TO or PATTERN_DEFAULT[config.pattern].to,
            DELAY = config.DELAY or DELAY,
            DURATION = config.DURATION or DURATION,
        })
    end

    if set_win_funcs['WIN_APPEND_ANIM_ELMT'] ~= nil then set_win_funcs['WIN_APPEND_ANIM_ELMT'](obj) end
  
       --带回调的动画进入
    local function _open(callfunc)
        --local actions = {}
        local doAnim = false
        for i, element in ipairs(obj.elements or {}) do
            doAnim = true
            local target = CCNodeExtend.extend(element.node)
            if element.PROP == NODE_PRO_X then
                element.node:setPositionX(element.FROM)
            elseif element.PROP == NODE_PRO_Y then
                element.node:setPositionY(element.FROM)
            end
            CCLuaLog('node position x = '..tostring(element.node:getPositionX()))
            if i == #(obj.elements or {}) then
                target:tweenFromTo(LINEAR_IN, element.PROP, element.DELAY, element.DURATION, 0, element.FROM, element.TO, 0, function()
                    if callfunc then callfunc() end 
                end)
            else
                target:tweenFromTo(LINEAR_IN, element.PROP, element.DELAY, element.DURATION, 0, element.FROM, element.TO, 0)
            end
        end 
        if not doAnim and callfunc then
            callfunc()
        end
            
    end

    --带回调的动画退出
    local function _exit(callfunc)
        --local actions = {}
        local doAnim = false
        for i, element in ipairs(obj.elements or {}) do
            doAnim = true
            local target = CCNodeExtend.extend(element.node)
            if i == #(obj.elements) then
                target:tweenFromTo(LINEAR_IN, element.PROP, element.DELAY, element.DURATION, 0, element.TO, element.FROM, 0, function() 
                    if callfunc then callfunc() end
                end)
            else
                target:tweenFromTo(LINEAR_IN, element.PROP, element.DELAY, element.DURATION, 0, element.TO, element.FROM, 0)
            end
        end
        if not doAnim and callfunc then callfunc() end
    end

    --打开
    function obj:open(callfunc)
        _open(callfunc)
    end

    --带回调的立即销毁接口
    function obj:destroy(callfunc)
        --TODO override
        schedule_frames(5, removeUnusedTextures)
    end

    --带回调的动画退出接口
    function obj:exit(callfunc)
        _exit(callfunc)
    end

    return obj
end


--++++++++++++++++++++++++++
--+      元件动画窗口      +
--++++++++++++++++++++++++++
--由baseAnimWin扩展
--@param: pwin 
--@param: winconfig: 
--@param: winname
--@param: set_win_callfunc
local animElementWinGroups = {}
function createAnimElementWin(pwin, winconfig, winname, set_win_callfunc)
    local baseObj = createBaseAnimElementWin(winconfig.baseWinMCName, winconfig.elements, set_win_callfunc or {})
    local mc = baseObj.mc
    local win = TLWindow:createWindow(mc)
    local obj = baseObj
    obj.win = win
    animElementWinGroups[obj] = 1
    g_historyManager.current_win = obj

    win:SetWindowName(winname or 'SLIDE ELEMENT BASE WIN'..winconfig.baseWinMCName)
    pwin:GetNode():addChild(mc)
    pwin:AddChildWindow(win)

    local reg = toFrame(P_MC)
    reg:setClipRegion(reg.mcBoundingBox)

    --override base destroy
    --带回调的立即销毁接口
    --local super_destroy = obj.destroy
    function obj:destroy(callfunc)
        --schedule_once(function()
            --pwin:GetNode():removeAllChildrenWithCleanup(true)
            animElementWinGroups[obj] = nil
            mc:removeFromParentAndCleanup(true)
            pwin:RemoveChildWindow(win)
            schedule_frames(5, removeUnusedTextures)
            win = nil
            mc = nil
            if callfunc then
                callfunc()
            end
        --end)
    end

    return obj
end

function destroyAllAnimElementWin()
    local hasCloseWin = false
    for obj, _ in pairs(animElementWinGroups or {}) do
        if toTLWindow(obj.win) ~= nil then
            obj:destroy(function() end)
            hasCloseWin = true
        end
    end
    animElementWinGroups = {}

    CCLuaLog('[utils/basewin]  destroy all slide element win, return '..tostring(hasCloseWin))
    return hasCloseWin
end



--++++++++++++++++++++++++++
--+    元件动画 Top窗口    +
--++++++++++++++++++++++++++
--top Window, 由baseAnimWin扩展
--@param: configs
--@param: winname
--@param: layer_node
--@param: z_order
--@param: set_win_callfunc 
local animElementTopWinGroups = {}
function createAnimElementTopWin(configs, winname, layer_node, z_order, set_win_callfunc)
    local obj = createBaseAnimElementWin(configs.baseWinMCName, configs.elements, set_win_callfunc or {})
    local mc = obj.mc
    local win = TLWindow:createWindow( mc, TL_WINDOW_UNIVARSAL )
    obj.win = win

    animElementTopWinGroups[obj] = 1

    -- 可以指定父节点以及 z_order 
    if layer_node ~= nil then layer_node:addChild( mc, z_order or 0 ) end
    win:SetIsVisible( false )
    TLWindowManager:SharedTLWindowManager():AddModuleWindow( win )
    --win:SetWindowName(winname or 'SLIDE ELEMENT BASE WIN'..winconfig.baseWinMCName)

    --inherit & override base destroy
    local super_open = obj.open
    function obj:open(callfunc)
        super_open(self, callfunc)
    end
    
    --override base destroy
    --带回调的立即销毁接口
    function obj:destroy(callfunc)
        --schedule_once(function()
            animElementTopWinGroups[obj] = nil
            win:GetNode():removeFromParentAndCleanup(true)
            TLWindowManager:SharedTLWindowManager():RemoveModuleWindow(win)
            schedule_frames(5, removeUnusedTextures)
            win = nil
            mc = nil
            if callfunc then
                callfunc()
            end
        --end)
    end

    return obj
end

function destroyAllAnimElementTopWin()
    local hasCloseWin = false
    for obj, _ in pairs(animElementTopWinGroups or {}) do
        if toTLWindow(obj.win) ~= nil then
            --
            obj:destroy()
            hasCloseWin = true
        end
    end
    animElementTopWinGroups = {}

    CCLuaLog('[utils/basewin]  destroy all slide element top win, return '..tostring(hasCloseWin))
    return hasCloseWin
end


--++++++++++++++++++++++++++
--+      英雄排序面板      +
--++++++++++++++++++++++++++
local heroSortWin = nil
local function createHeroSortWin(callfunc)
    local win = topwindow('UI/game_parts/parts10_23')
    local mc = win:GetNode()
    win:SetWindowPos(TLWindowManager:SharedTLWindowManager():GetCenterPoint())
    local obj = {
        win = win,
        mc = mc, 
    }

    local mc_controlBoard = createMovieClipWithName('UI/game_parts/parts6_21')
    mc:getChildByName('quyu'):addChild(mc_controlBoard)

    local oldHeroSortType = g_pets_sortconfig.first
    local btngroups = selectbox_group()
    for _, btnconfig in table.orderIter(HERO_SORT_TYPE, function(a, b) return a.value.index < b.value.index end) do
        local selbox = select_box(win, 'anniu'..btnconfig.index)
        function selbox:onchange()
            g_pets_sortconfig.first = btnconfig.name
            toMovieClip(self.movieclip:getChildByName('anniu')):play(self.checked and 1 or 0, 0)
        end
        selbox.movieclip:getChildByName('zi'):addChild(MCLoader:sharedMCLoader():loadSpriteAsync(btnconfig.icon))
        selbox.movieclip:setAutoClear(false)
        selbox.type = btnconfig.name
        btngroups:add(selbox)
    end
    --mc_controlBoard:getChildByName('anniu7'):setVisible(false)
    mc_controlBoard:getChildByName('anniu8'):setVisible(false)
    mc_controlBoard:getChildByName('anniu9'):setVisible(false)
    btngroups:change(HERO_SORT_TYPE[g_pets_sortconfig.first].index)


    local win_ok = TLWindow:createWindow(mc:getChildByName('queding'))
    win:AddChildWindow(win_ok)
    mc:getChildByName('queding'):getChildByName('wenben'):addChild(MCLoader:sharedMCLoader():loadSpriteAsync('word_e005.png'))
    button(win_ok, 'anniu', function() obj:destroy(callfunc) end)

    function obj:open()
        win:SetIsVisible(true)
        TLWindowManager:SharedTLWindowManager():MoveToTop(win)
    end

    function obj:destroy(callfunc)
        schedule_once(function()
            win:GetNode():removeFromParentAndCleanup(true)
            TLWindowManager:SharedTLWindowManager():RemoveModuleWindow(win)
            win = nil
            mc = nil
            if callfunc then
                callfunc()
            end
        end)
    end

    return obj
end

function _openHeroSortWin(callfunc)
    if heroSortWin == nil or toTLWindow(heroSortWin.win) == nil then
        heroSortWin = createHeroSortWin(callfunc)
    end
    heroSortWin:open()
end


--++++++++++++++++++++++++++
--+         编辑框         +
--++++++++++++++++++++++++++
function openEditorBox(title, default, placeholder, okcallfunc, wordsLimit, tip)
    local default = default or ''
    placeholder = ((placeholder or '') ~= '') and placeholder or _YYTEXT('请在此输入')
    local editor
    local btn_ok
    local function createEditor(content_info, size, count)
        local node = MCFrame:createWithBox(CCRect(-size.width/2, -size.height/2, size.width, size.height))
        local win = TLWindow:createWindow(node)

        -- 输入框
        local editor_frame = MCFrame:createWithBox(CCRect(-size.width/2, -25, size.width, 50))
        local editor_win = TLWindow:createEditLabel(editor_frame, TL_WINDOW_INPUT, 24, placeholder )
        editor = init_text_input(editor_win, false, function()
            btn_ok:enable((editor:getinput() or '') ~= '')
        end, onbuttondown)
        editor.c_win:SetInputCursorSize(2, 2)
        editor.c_win:SetInputLimit(wordsLimit or 140)
        editor.c_win:SetInputTextModel(5)
        node:addChild(editor_frame)
        win:AddChildWindow(editor_win)

        -- 提示文本
        local frame_tip = MCFrame:createWithBox(CCRect(0, 0, size.width, 30))
        init_label(frame_tip, 20, CCImage.kAlignCenter):set_rich_string(tip or '')
        node:addChild(frame_tip)
        frame_tip:setPositionY(- size.height / 2 - 15)

        return node, win
    end

    openMessageBox(title, nil, 'MB_OKCANCEL', {
        CONTENT_STYLE = function(content_info)
            table.insert(content_info, {
                text = '',
                alignment = CCImage.kAlignCenter,
                oncreate = createEditor,
            })
            return content_info
        end,

        MB_INIT = function(obj)
            obj.frame:setPositionY(TLWindowManager:SharedTLWindowManager():GetCenterPoint().y + getBoundingBox(obj.frame).size.height / 2)
            btn_ok = obj.btns[2]
            btn_ok:enable(false)
            --setControlGrey(btn_ok.movieclip, true)
            editor.c_win:SetInputText(default)
        end,

        MB_OK = function()
            if okcallfunc then okcallfunc(editor:getinput()) end
        end,
    })
end
