package com.cdwater.petmall.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 后台宠物订单分页VO
 */
@Data
public class PetOrdersPageVO {

    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderNo;

    private Integer orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    private String petName;

    private String petImg;

    private BigDecimal petPrice;

    private String consignee;

    private String phoneNumber;

    private String provinceCode;

    private String cityCode;

    private String districtCode;

    private String detailAddress;

    private String userNickname;

    private String shopName;
}