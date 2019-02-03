package com.ldy.async;

import com.alibaba.fastjson.JSONObject;
import com.ldy.utils.JedisAdapter;
import com.ldy.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 将事件发送到jedisAdapter队列中
 */
@Service
public class EventProducer {
    @Autowired
    private  JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}














