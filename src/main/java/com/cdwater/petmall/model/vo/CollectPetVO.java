package com.cdwater.petmall.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 收藏夹展示宠物VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectPetVO {
    private Integer id;
    private Integer petId;
    private String petName;
    private Integer petGender;
    private String petImg;
    private BigDecimal petPrice;
    private Integer petStore;
    private Integer petSaleStatus;
}
