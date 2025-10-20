package com.cdwater.petmall.interceptor;

import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.JwtUtil;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取http请求头的token
        String token = request.getHeader(jwtProperties.getTokenName());
        // token不存在，抛出异常
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ReturnMsg.INVALID_TOKEN);
        }
        //判断token是否在redis白名单
        String tokenStatus = redisUtil.getStr(RedisMark.TOKEN_PREFIX + token);
        if (!StringUtils.equals(tokenStatus, RedisMark.WHITE)) {
            throw new BusinessException(ReturnMsg.INVALID_TOKEN);
        }
        // 验证token是否合法
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecret(), token);
            // 解析token获取账号信息
            Integer id = (Integer) claims.get("id");
            Integer role = (Integer) claims.get("role");
            // 将当前登录账号信息存入ThreadLocal，便于后续业务获取
            ThreadUtil.setId(id);
            ThreadUtil.setRole(role);
        } catch (Exception e) {
            throw new BusinessException(ReturnMsg.INVALID_TOKEN);
        }

        //放行
        return true;
    }
}
