package com.cdwater.petmall.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物订单展示VO
 */
@Data
public class PetOrdersShowVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderNo;

    private Integer orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    private Integer shopId;

    private Integer petId;

    private String petName;

    private String petImg;

    private BigDecimal petPrice;

    private String shopName;
}
