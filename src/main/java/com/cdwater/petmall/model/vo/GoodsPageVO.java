package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 后台宠物用品分页VO
 */
@Data
public class GoodsPageVO {
    private Integer id;
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
