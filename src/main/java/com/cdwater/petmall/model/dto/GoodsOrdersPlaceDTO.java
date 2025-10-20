package com.cdwater.petmall.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class GoodsOrdersPlaceDTO {
    private Integer userId;
    private Integer addressId;
    private List<SelectGoodsItem> selectGoods;

    @Data
    public static class SelectGoodsItem {
        private Integer goodsId;
        private Integer quantity;
    }
}
