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
}
