--./utils/historyManager.lua
require 'utils.table'

g_historyManager = {}
g_historyManager.all_history = {}
g_historyManager.current_win = nil

function g_historyManager:push( callback )
    table.insert(self.all_history,callback)
end


function g_historyManager:clear()
    self.all_history = {}
end

function g_historyManager:goback()
    local history_callback = table.pop(self.all_history)
    if history_callback then
        history_callback()
    else
        openFBMainWin()
    end
end

function g_historyManager:destroy_current_win(callfunc, systeminfo)
    if self.current_win ~= nil and toTLWindow( self.current_win.win ) ~= nil then
        self.current_win:destroy(callfunc, systeminfo)
        --FIXME 不缺定是否要将current_win设为nil
        --self.current_win = nil
    elseif callfunc then 
        callfunc()
    end

end
