package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 宠物详情VO
 */
@Data
public class PetDetailVO {
    private Integer id;
    private Integer shopId;
    private String name;
    private Integer gender;
    private String img;
    private BigDecimal price;
    private Integer store;
    private String introduce;
    private String content;
    private Integer saleStatus;
    private String shopName;
    private String shopAvatar;
    private String typeName;
    private Boolean hasCollect;
}
