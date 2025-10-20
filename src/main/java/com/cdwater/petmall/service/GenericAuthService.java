package com.cdwater.petmall.service;

import com.cdwater.petmall.model.auth.*;

public interface GenericAuthService<T> {
    LoginResponse login(LoginRequest loginRequest,Class<T> entityClass,long tokenExpiration);

    void register(RegisterRequest registerRequest,Class<T> entityClass);

    LoginResponse phoneLogin(LoginRequest loginRequest, Class<T> entityClass, long tokenExpiration);

    void forget(ForgetRequest forgetRequest, Class<T> entityClass);

    void change(ChangeRequest changeRequest, Class<T> entityClass);
}
