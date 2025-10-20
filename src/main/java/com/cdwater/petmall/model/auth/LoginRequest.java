package com.cdwater.petmall.model.auth;

import lombok.Data;

/**
 * 登录请求
 */
@Data
public class LoginRequest {
    //角色认证
    private Integer role;
    //账号登录
    private String username;
    private String password;

    //手机号登录
    private String phone;
    private String code;
}
