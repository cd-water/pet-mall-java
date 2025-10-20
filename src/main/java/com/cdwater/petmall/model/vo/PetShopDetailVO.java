package com.cdwater.petmall.model.vo;

import lombok.Data;

/**
 * 宠物店详情VO
 */
@Data
public class PetShopDetailVO {
    private Integer id;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String detailAddress;
    private String introduce;
}
