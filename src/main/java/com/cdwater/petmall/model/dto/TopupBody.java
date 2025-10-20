package com.cdwater.petmall.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopupBody {
    private Integer userId;
    private BigDecimal topupAmount;
}
