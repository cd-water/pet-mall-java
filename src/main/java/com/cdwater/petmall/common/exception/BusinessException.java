package com.cdwater.petmall.common.exception;

import com.cdwater.petmall.common.enums.ReturnMsg;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;
    private final String msg;

    public BusinessException(ReturnMsg returnMsg) {
        this.code = returnMsg.getCode();
        this.msg = returnMsg.getMsg();
    }
}
