package com.cdwater.petmall.common.constants;

/**
 * 文本信息
 */
public class TextInfo {
    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "Aa123456";

    /**
     * 默认昵称前缀
     */
    public static final String DEFAULT_PETSHOP_NICKNAME_PREFIX = "petshop_";
    public static final String DEFAULT_USER_NICKNAME_PREFIX = "user_";

    /**
     * 默认昵称后缀长度
     */
    public static final int DEFAULT_NICKNAME_SUFFIX_LENGTH = 7;

    /**
     * 初始购物车商品数量
     */
    public static final Integer INIT_CART_QUANTITY = 1;

    /**
     * 默认地址
     */
    public static final Integer IS_DEFAULT_ADDRESS = 1;

    /**
     * 删除标记
     */
    public static final Integer IS_DELETED = 1;

    /**
     * 审核状态-待提交、审核通过
     */
    public static final Integer WAIT_SUMMIT = 0;
    public static final Integer AUDIT_SUCCESS = 2;

    /**
     * 销售状态-售卖中
     */
    public static final Integer ON_SALE = 1;
}
