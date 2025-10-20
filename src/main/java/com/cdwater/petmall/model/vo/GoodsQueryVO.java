package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 后台宠物用品查询VO
 */
@Data
public class GoodsQueryVO {
    private Integer id;
    private Integer typeId;
    private Integer shopId;
    private String name;
    private String img;
    private BigDecimal price;
    private Integer store;
    private String introduce;
    private Integer saleVolume;
    private Integer saleStatus;
}
