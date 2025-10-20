package com.cdwater.petmall.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 宠物分页DTO
 */
@Data
public class PetPageDTO {
    //分页参数
    private Integer pageNum;
    private Integer pageSize;

    //搜索条件
    private String name;
    private Integer typeId;
    private Boolean onlyInStock;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String shopName;
    private Integer saleStatus;
    private Integer recommend;

    //标记区分是否限定宠物店范围
    private Integer shopId;
}
