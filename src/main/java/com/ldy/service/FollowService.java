package com.ldy.service;

import com.ldy.utils.JedisAdapter;
import com.ldy.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    private  JedisAdapter jedisAdapter;

    /**
     * 用户关注了某个实体,实体可以是问题,用户,评论等任何实体
     *
     * @param userId     用户的ID
     * @param entityType 实体的类型
     * @param entityId   实体的ID
     * @return
     */
    public boolean follow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        // 实体的粉丝增加当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.multi(jedis);//事务执行开始
        transaction.zadd(followerKey, date.getTime(), String.valueOf(userId));// 粉丝+1
        transaction.zadd(followeeKey, date.getTime(), String.valueOf(entityId));//关注+1
        List<Object> ret = jedisAdapter.exec(transaction, jedis);//事务开始执行
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unFollow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 实体的粉丝增加当前用户
        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.multi(jedis);
        transaction.zrem(followerKey, String.valueOf(userId));// 实体的粉丝移除当前用户
        transaction.zrem(followeeKey, String.valueOf(entityId));// 当前对这类实体关注-1
        List<Object> ret = jedisAdapter.exec(transaction, jedis);// 执行事物
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followerKey, 0, offset + count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followeeKey, 0, offset + count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }


    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }


    private List<Integer> getIdsFormSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }


    /**
     * 判断用户是否关注了某个实体
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }


}

















