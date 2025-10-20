package com.cdwater.petmall.model.auth;

import lombok.Data;

/**
 * 修改密码请求
 */
@Data
public class ChangeRequest {
    //身份参数
    private Integer id;
    private Integer role;
    //密码参数
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
