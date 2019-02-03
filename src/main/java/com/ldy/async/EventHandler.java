package com.ldy.async;

import java.util.List;

public interface EventHandler {
    /**
     * 处理Event
     * @param model
     */
    void doHandle(EventModel model);

    /**
     * 对哪些Event关心
     * @return
     */
    List<EventType> getSupportEventTypes();
}
