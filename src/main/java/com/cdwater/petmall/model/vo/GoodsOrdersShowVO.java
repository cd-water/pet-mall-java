package com.cdwater.petmall.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品订单展示VO
 */
@Data
public class GoodsOrdersShowVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderNo;

    private Integer orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    private Integer quantity;

    private BigDecimal totalPrice;

    private Integer shopId;

    private String goodsName;

    private String goodsImg;

    private BigDecimal goodsPrice;

    private String shopName;
}
