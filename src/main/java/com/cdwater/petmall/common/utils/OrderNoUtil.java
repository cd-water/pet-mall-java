package com.cdwater.petmall.common.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class OrderNoUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1735689600L;
    /**
     * 序列号位数
     */
    private static final int COUNT_BITS = 32;

    public long nextId(String orderType) {
        //生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        //生成序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long increment = redisTemplate.opsForValue().increment("orderNo:" + orderType + ":" + date);

        //拼接返回
        return timestamp << COUNT_BITS | increment;
    }
}
