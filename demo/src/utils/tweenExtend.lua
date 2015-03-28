-- ./utils/tweenExtend.lua
require 'utils.class'

tweenExtend = { all_tween_handles = {} }
function tweenExtend:removeTween( handle )
    TLTweenManager:sharedTLTweenManager():removeTween( handle )
    self.all_tween_handles[handle] = nil
end

function tweenExtend:removeAllTween()
    for handle,_ in ipairs( self.all_tween_handles ) do
        TLTweenManager:sharedTLTweenManager():removeTween( handle )
    end
    self.all_tween_handles = {}
end

-- from to ----------------------------------------------------------------------------------------------------------------------------------------------------------
function tweenExtend:tweenFromToOnce( ease_id, delay, duration, from, to, call_back_close, set_property_func )
    return self:tweenFromTo( ease_id, delay, duration, 0, from, to, 0, call_back_close, set_property_func )
end

function tweenExtend:tweenFromToEx( ease_id, delay, duration, from, to, loop_count, call_back_close, set_property_func )
    return self:tweenFromTo( ease_id, delay, duration, 0, from, to, loop_count, call_back_close, set_property_func )
end

function tweenExtend:tweenFromTo( ease_id, delay, duration, interval, from, to, loop_count, call_back_close, set_property_func )
    call_back_close = call_back_close or function() end
    local handle = TLTweenManager:sharedTLTweenManager():tweenFromTo( ease_id, delay, duration, interval, from, to, loop_count, set_property_func, call_back_close )

    self.all_tween_handles[handle] = 1

    return handle
end


