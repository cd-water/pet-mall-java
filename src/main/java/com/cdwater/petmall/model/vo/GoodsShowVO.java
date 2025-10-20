package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 展示宠物用品VO
 */
@Data
public class GoodsShowVO {
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
    private String typeName;
    private String shopName;
}