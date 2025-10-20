package com.cdwater.petmall.entity;

import lombok.Data;

/**
 * 用户地址实体类
 */
@Data
public class Address {

    private Integer id;

    private Integer userId;

    private String consignee;

    private String phoneNumber;

    private String provinceCode;

    private String cityCode;

    private String districtCode;

    private String detailAddress;

    private String tag;

    private Integer isDefault;
}