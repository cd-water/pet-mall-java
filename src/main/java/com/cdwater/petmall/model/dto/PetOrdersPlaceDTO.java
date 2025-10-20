package com.cdwater.petmall.model.dto;

import lombok.Data;

@Data
public class PetOrdersPlaceDTO {
    private Integer userId;
    private Integer shopId;
    private Integer petId;
    private Integer addressId;
}
