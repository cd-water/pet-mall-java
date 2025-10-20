package com.cdwater.petmall.controller;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.auth.*;
import com.cdwater.petmall.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 账号登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return Result.success(loginResponse);
    }

    /**
     * 发送验证码
     */
    @PostMapping("/code")
    public Result sendCode(@RequestBody CodeRequest codeRequest) {
        String code = authService.sendCode(codeRequest);
        log.info("{}的验证码：{}", codeRequest.getPhone(), code);
        return Result.success(code);
    }

    /**
     * 手机号登录
     */
    @PostMapping("/phoneLogin")
    public Result phoneLogin(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.phoneLogin(loginRequest);
        return Result.success(loginResponse);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return Result.success();
    }

    /**
     * 忘记密码
     */
    @PostMapping("/forget")
    public Result forget(@RequestBody ForgetRequest forgetRequest) {
        authService.forget(forgetRequest);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/change")
    public Result change(@RequestBody ChangeRequest changeRequest) {
        authService.change(changeRequest);
        return Result.success();
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        authService.logout(request);
        return Result.success();
    }
}
