package com.nowcoder.community.util;

public class RedisKeyUtil implements CommunityConstant{

//    private static final String SPLIT = ":";
//
//    private static final String PREFIX_ENTITY_LIKE = "like:entity";
//    private static final String PREFIX_USER_LIKE ="like:user";
//    private static final String PREFIX_FOLLOWEE ="followee";
//    private static final String PREFIX_FOLLOWER ="follower";

    /**
     * 生成实体“赞”的key
     * like:entity:entityType:entityId -> set（userId, )
     */
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞总数
     */
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 关注，取关
     *
     *  某个用户关注的实体
     *  followee:userId:entityType -> Zset(entityId, now)
     *
     *  用户id：关注的实体类型
     */

    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     *  某个实体（帖子，用户）拥有的粉丝
     *  follower:entityType:entityId -> zset(userId, now)
     *
     *  实体类型（本人）：实体id
     */

    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
