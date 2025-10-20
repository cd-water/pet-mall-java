package com.cdwater.petmall.common.constants;

/**
 * 订单状态
 */
public class OrderStatus {
    // 待付款
    public static final Integer PENDING_PAYMENT = 0;

    // 待接单
    public static final Integer PENDING_ACCEPT = 1;

    // 派送中
    public static final Integer DELIVERING = 2;

    // 已送达
    public static final Integer DELIVERED = 3;

    // 已完成
    public static final Integer COMPLETED = 4;

    // 已取消
    public static final Integer CANCELLED = 5;
}
