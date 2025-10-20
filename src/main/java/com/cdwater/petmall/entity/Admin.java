package com.cdwater.petmall.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 管理员实体类
 */
@Data
public class Admin {

    private Integer id;

    private String nickname;

    private String avatar;

    private String username;

    private String password;

    private Integer role;

    private String phone;

    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Integer isDeleted;
}