package com.cdwater.petmall.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PetGroupVO {
    private Integer shopId;
    private String shopName;
    private List<PetItem> petList;

    @Data
    public static class PetItem {
        private Integer petId;
        private String petName;
    }
}
