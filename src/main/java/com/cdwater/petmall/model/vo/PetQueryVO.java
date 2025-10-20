package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 后台宠物查询VO
 */
@Data
public class PetQueryVO {
    private Integer id;
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
