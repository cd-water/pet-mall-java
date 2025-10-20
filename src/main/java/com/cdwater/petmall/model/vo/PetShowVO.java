package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 展示宠物VO
 */
@Data
public class PetShowVO {
    private Integer id;
    private String name;
    private Integer gender;
    private String img;
    private BigDecimal price;
    private Integer store;
    private Integer saleStatus;
}
