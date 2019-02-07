package com.ldy.utils;

/**
 * 所有的redis key都从这里生成，保证key不会重复
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_TIMELINE = "TIMELINE";

    // 获取粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // 关注的对象
    private static String BIZ_FOLOWEE = "FOLLOWEE";

    /**
     * 根据事务创建一个likeKey名
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    /**
     * 某个用户的粉丝key
     *
     * @param entityType 实体类型
     * @param entityId   实体ID
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    /**
     * 每个用户对某类实体的关注key
     *
     * @param userId     用户的ID
     * @param entityType 实体类型
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }


    public static String getTimeline(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }


}















