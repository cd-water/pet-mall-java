package com.cdwater.petmall.common.constants;

import java.util.*;

/**
 * redis记号
 */
public class RedisMark {
    /**
     * 验证码缓存标记
     * key格式：code:{phone}; value格式：string(验证码字符串)
     */
    public static final String CODE_PREFIX = "code:";
    public static final Integer CODE_TTL = 60;// second

    /**
     * token缓存标记
     * key格式：token:{token}; value格式：string(__white__)
     */
    public static final String TOKEN_PREFIX = "token:";
    public static final String WHITE = "__white__";

    /**
     * 用户信息缓存标记
     * key格式：profile:{token}; value格式：hash(字段->值)
     */
    public static final String PROFILE_PREFIX = "profile:";

    /**
     * 空缓存标记（缓存穿透解决策略）
     */
    public static final String EMPTY = "__empty__";
    public static final Map<String, String> EMPTY_MAP = Map.of(EMPTY, EMPTY);
    public static final Integer EMPTY_TTL = 5 * 60;// second

    /**
     * 系统公告缓存标记
     * key格式：notice; value格式：json(存对象)
     */
    public static final String NOTICE_KEY = "notice";
    public static final Integer NOTICE_TTL = 6 * 60 * 60;// second

    /**
     * 轮播图缓存标记
     * key格式：slideshow; value格式：hash({petId}->json(存对象))
     */
    public static final String SLIDESHOW_KEY = "slideshow";
    public static final Integer SLIDESHOW_TTL = 4 * 60 * 60;// second

    /**
     * 宠物类型缓存标记
     * all key格式：petType:all; value格式：json(存对象列表)
     * hot key格式：petType:hot; value格式：json(存对象列表)
     */
    public static final String PET_TYPE_ALL_KEY = "petType:all";
    public static final Integer PET_TYPE_ALL_TTL = 6 * 60 * 60;// second
    public static final String PET_TYPE_HOT_KEY = "petType:hot";
    public static final Integer PET_TYPE_HOT_TTL = 60 * 60;// second


    /**
     * 商品类型缓存标记
     * all key格式：goodsType:all; value格式：json(存对象列表)
     * hot key格式：goodsType:hot; value格式：json(存对象列表)
     */
    public static final String GOODS_TYPE_ALL_KEY = "goodsType:all";
    public static final Integer GOODS_TYPE_ALL_TTL = 6 * 60 * 60;// second
    public static final String GOODS_TYPE_HOT_KEY = "goodsType:hot";
    public static final Integer GOODS_TYPE_HOT_TTL = 60 * 60;// second

    /**
     * 宠物缓存标记
     * detail key格式：pet:detail:{petId}; value格式：hash(字段->值)
     * recommend key格式：pet:recommend; value格式：hash({petId}->json(存对象))
     */
    public static final String PET_DETAIL_PREFIX = "pet:detail:";
    public static final Integer PET_DETAIL_TTL = 30 * 60;// second
    public static final String PET_RECOMMEND_KEY = "pet:recommend";
    public static final Integer PET_RECOMMEND_TTL = 60 * 60;// second

    /**
     * 商品缓存标记
     * hot key格式：goods:hot; value格式：hash({goodsId}->json(存对象))
     */
    public static final String GOODS_HOT_KEY = "goods:hot";
    public static final Integer GOODS_HOT_TTL = 60 * 60;// second

    /**
     * 宠物店缓存标记
     * detail key格式：petShop:detail:{petShopId}; value格式：hash(字段->值)
     * hot key格式：petShop:hot; value格式：hash({petShopId}->json(存对象))
     */
    public static final String PET_SHOP_DETAIL_PREFIX = "petShop:detail:";
    public static final Integer PET_SHOP_DETAIL_TTL = 30 * 60;// second
    public static final String PET_SHOP_HOT_KEY = "petShop:hot";
    public static final Integer PET_SHOP_HOT_TTL = 60 * 60;// second

    /**
     * 用户缓存标记
     * collect key格式：user:{userId}:collect; value格式：hash({petId}->json(存对象))
     * cart key格式：user:{userId}:cart; value格式：hash({goodsId}->json(存对象))
     * address key格式：user:{userId}:address; value格式：json(存对象列表)
     * balance key格式：user:{userId}:balance; value格式：string(余额字符串)
     */
    public static final String USER = "user";
    public static final String CART = "cart";
    public static final String COLLECT = "collect";
    public static final String ADDRESS = "address";
    public static final String BALANCE = "balance";
    public static final Integer CART_TTL = 30 * 60;
    public static final Integer COLLECT_TTL = 30 * 60;
    public static final Integer ADDRESS_TTL = 30 * 60;
    public static final Integer BALANCE_TTL = 5 * 60;

    /**
     * 订单号递增标记
     */
    public static final String PET_ORDERS = "petOrders";
    public static final String GOODS_ORDERS = "goodsOrders";
}
