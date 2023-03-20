package com.nowcoder.community.util;

public interface CommunityConstant {

    /**
     * 激活状态
     */
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认登录超时时间，不选’记住我‘
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * ‘记住我’登录有效时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 帖子实体类型
     */
    int ENTITY_TYPE_POST=1;

    /**
     * 评论实体类型
     */
     int ENTITY_TYPE_COMMENT=2;
     /**
     *  用户实体类型
     */
     int ENTITY_TYPE_USER=3;
}
