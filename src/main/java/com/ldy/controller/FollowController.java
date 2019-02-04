package com.ldy.controller;

import com.ldy.async.EventModel;
import com.ldy.async.EventProducer;
import com.ldy.async.EventType;
import com.ldy.model.*;
import com.ldy.service.CommentService;
import com.ldy.service.FollowService;
import com.ldy.service.QuestionService;
import com.ldy.service.UserService;
import com.ldy.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Autowired
    private  FollowService followService;

    @Autowired
    private  CommentService commentService;

    @Autowired
    private  QuestionService questionService;

    @Autowired
    private  UserService userService;

    @Autowired
    private  HostHolder hostHolder;

    @Autowired
    private  EventProducer eventProducer;

    @RequestMapping(path = "/followUser", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        // 提交到异步队列
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        // 返回关注的人数
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    @RequestMapping(path = "/unfollowUser", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        // 提交到异步队列
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        // 返回关注的人数
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    @RequestMapping(path = "/followQuestion", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question question = questionService.getById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        int userId=hostHolder.getUser().getId();
        boolean ret = followService.follow(userId, EntityType.ENTITY_QUESTION, questionId);

        // 提交到异步队列
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(userId).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(question.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


    @RequestMapping(path = "/unfollowQuestion", method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question question = questionService.getById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        // 提交到异步队列
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(question.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


    /**
     * 取出要显示的followers
     * @param model
     * @param userid
     * @return
     */
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userid) {
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userid, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userid));
        model.addAttribute("curUser", userService.getUser(userid));
        return "followers";
    }


    /**
     * 取出要显示的followees
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model,@PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER,0,10);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer userId : userIds) {
            User user = userService.getUser(userId);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(userId));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, userId));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;

    }


}






















