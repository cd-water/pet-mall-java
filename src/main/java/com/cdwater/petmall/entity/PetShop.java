package com.cdwater.petmall.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 宠物店实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetShop {

    private Integer id;

    private String nickname;

    private String avatar;

    private String username;

    private String password;

    private Integer role;

    private String phone;

    private String email;

    private String provinceCode;

    private String cityCode;

    private String districtCode;

    private String detailAddress;

    private String introduce;

    private String practiceLicense;

    private String principalName;

    private String principalNo;

    private Integer auditStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Integer isDeleted;
}