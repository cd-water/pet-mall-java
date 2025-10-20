package com.cdwater.petmall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回信息枚举
 */
@AllArgsConstructor
@Getter
public enum ReturnMsg {
    SUCCESS_REQUEST(200, "请求成功"),
    ILLEGAL_INPUT(400, "非法输入"),
    INVALID_TOKEN(401, "凭证无效或已过期"),
    FORBIDDEN_ACCESS(403, "无访问权限"),
    NOT_FOUND(404, "资源未找到"),
    SYSTEM_ERROR(500, "系统异常"),
    //自定义错误码
    LOGIN_FAILED(666, "账号或密码错误"),
    PHONE_NOT_REGISTER(666, "手机号未注册"),
    PHONE_REGISTER(666, "手机号已注册"),
    CODE_ERROR(666, "验证码错误"),
    ACCOUNT_REGISTER(666, "账号已注册"),
    OLD_PASSWORD_ERROR(666, "原密码错误"),
    Type_EXISTED(666, "类型已存在"),
    COLLECT_EXISTED(666, "收藏夹中已存在"),
    INSUFFICIENT_BALANCE(888, "余额不足，请先充值"),
    STOCK_NOT_ENOUGH(666, "库存不足");

    private final Integer code;
    private final String msg;
}
