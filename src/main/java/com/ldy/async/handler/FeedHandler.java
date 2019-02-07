package com.ldy.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.ldy.async.EventHandler;
import com.ldy.async.EventModel;
import com.ldy.async.EventType;
import com.ldy.model.EntityType;
import com.ldy.model.Feed;
import com.ldy.model.Question;
import com.ldy.model.User;
import com.ldy.service.FeedService;
import com.ldy.service.FollowService;
import com.ldy.service.QuestionService;
import com.ldy.service.UserService;
import com.ldy.utils.JedisAdapter;
import com.ldy.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private QuestionService questionService;

    private String buildFeedData(EventModel model) {
        HashMap<String, String> map = new HashMap<>();
        // 触发用户都是通用的
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(model.getActorId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if (model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) {
        // 为了测试,把model的user的modelId随机一下
        Random random = new Random();
        model.setActorId(1 + random.nextInt(10));

        // 构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));
        if (feed.getData() == null) {
            // 不支持的feed
            return;
        }
        feedService.addFeed(feed);

        // 获取所有的粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);

        //系统队列
        followers.add(0);

        // 给所有的粉丝推事件
        for (Integer follower : followers) {
            String timelineKey = RedisKeyUtil.getTimeline(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
            // 限制最长的长度,如果timeLineKey的长度过于大,就删除后面的额新鲜事

        }
    }

    /**
     * 评论与关注触发事件
     * @return
     */
    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }
}























