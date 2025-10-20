package com.cdwater.petmall.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车展示宠物用品VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartGoodsVO {
    private Integer id;
    private Integer userId;
    private Integer quantity;
    private Integer goodsId;
    private Integer shopId;
    private String goodsName;
    private String goodsImg;
    private BigDecimal goodsPrice;
    private Integer goodsStore;
    private Integer goodsSaleStatus;
}
