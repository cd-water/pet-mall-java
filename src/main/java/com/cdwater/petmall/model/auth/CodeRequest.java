package com.cdwater.petmall.model.auth;

import lombok.Data;

/**
 * 获取验证码请求
 */
@Data
public class CodeRequest {
    private String phone;
}
