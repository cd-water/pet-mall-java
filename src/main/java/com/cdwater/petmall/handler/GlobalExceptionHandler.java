package com.cdwater.petmall.handler;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        log.error(e.getMessage());
        return Result.error();
    }

    @ExceptionHandler(BusinessException.class)
    public Result error(BusinessException e) {
        return Result.error(e.getCode(), e.getMsg());
    }
}
