package com.cdwater.petmall.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物订单下单响应VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetOrdersPlaceVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderNo;
    private Integer orderStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    private BigDecimal orderAmount;
}
