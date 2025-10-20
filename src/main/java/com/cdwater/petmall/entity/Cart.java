package com.cdwater.petmall.entity;

import lombok.Data;

/**
 * 购物车实体类
 */
@Data
public class Cart {

    private Integer id;

    private Integer userId;

    private Integer quantity;

    private Integer goodsId;
}