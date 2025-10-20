package com.cdwater.petmall.common;

import com.cdwater.petmall.common.enums.ReturnMsg;
import lombok.Data;

/**
 * 统一返回结果
 */
@Data
public class Result {

    private Integer code;
    private String msg;
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.setCode(ReturnMsg.SUCCESS_REQUEST.getCode());
        result.setMsg(ReturnMsg.SUCCESS_REQUEST.getMsg());
        return result;
    }

    public static Result success(Object data) {
        Result result = success();
        result.setData(data);
        return result;
    }

    public static Result error() {
        Result result = new Result();
        result.setCode(ReturnMsg.SYSTEM_ERROR.getCode());
        result.setMsg(ReturnMsg.SYSTEM_ERROR.getMsg());
        return result;
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
