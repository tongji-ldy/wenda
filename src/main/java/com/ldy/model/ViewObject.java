package com.ldy.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 将要显示的一个个问题内容集合到一个类中，方便前端调用显示
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<>();

    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key){
        return objs.get(key);
    }
}
