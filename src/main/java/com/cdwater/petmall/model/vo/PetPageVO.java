package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 后台宠物分页VO
 */
@Data
public class PetPageVO {
    private Integer id;
    private String name;
    private Integer gender;
    private String img;
    private BigDecimal price;
    private Integer store;
    private String introduce;
    private String content;
    private Integer saleStatus;
    private Integer recommend;
    private String shopName;
    private String typeName;
}