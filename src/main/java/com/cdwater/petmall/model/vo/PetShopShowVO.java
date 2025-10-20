package com.cdwater.petmall.model.vo;

import lombok.Data;

/**
 * 展示宠物店VO
 */
@Data
public class PetShopShowVO {
    private Integer id;
    private String nickname;
    private String avatar;
    private String phone;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String detailAddress;
}
