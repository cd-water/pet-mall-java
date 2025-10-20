package com.cdwater.petmall.entity;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 宠物实体类
 */
@Data
public class Pet {

    private Integer id;

    private Integer shopId;

    private Integer typeId;

    private String name;

    private Integer gender;

    private String img;

    private BigDecimal price;

    private Integer store;

    private String introduce;

    private String content;

    private Integer saleStatus;

    private Integer recommend;
}