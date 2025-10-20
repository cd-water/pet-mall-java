package com.cdwater.petmall.model.auth;

import lombok.Data;

/**
 * 找回密码请求
 */
@Data
public class ForgetRequest {
    private String phone;
    private String code;
    private String newPassword;
    private String confirmNewPassword;
    private Integer role;
}
