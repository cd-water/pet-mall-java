package com.cdwater.petmall.entity;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 宠物用品实体类
 */
@Data
public class Goods {

    private Integer id;

    private Integer shopId;

    private Integer typeId;

    private String name;

    private String img;

    private BigDecimal price;

    private Integer store;

    private String introduce;

    private Integer saleVolume;

    private Integer saleStatus;
}