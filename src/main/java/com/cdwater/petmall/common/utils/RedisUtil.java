package com.cdwater.petmall.common.utils;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 为TTL设置随机波动值
     */
    private long setRandomTTL(long ttl) {
        //设置随机过期时间，防止缓存雪崩
        double randomRate = 0.7 + RandomUtils.nextDouble(0, 0.6);
        return (long) (ttl * randomRate);
    }

    //缓存操作------------------------------------------------------------------------------------------------------------

    /**
     * 缓存为字符串
     */
    public void setStr(String key, String value, long ttl, TimeUnit unit, boolean fixedTTL) {
        if (StringUtils.isBlank(value)) return;
        if (fixedTTL) {
            redisTemplate.opsForValue().set(key, value, ttl, unit);
        } else {
            redisTemplate.opsForValue().set(key, value, setRandomTTL(ttl), unit);
        }
    }

    /**
     * 缓存对象为json
     */
    public <T> void setJson(String key, T obj, long ttl, TimeUnit unit, boolean fixedTTL) {
        if (obj == null) return;
        if (fixedTTL) {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(obj), ttl, unit);
        } else {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(obj), setRandomTTL(ttl), unit);
        }
    }

    /**
     * 缓存对象列表为json
     */
    public <T> void setJson(String key, List<T> list, long ttl, TimeUnit unit, boolean fixedTTL) {
        if (CollectionUtils.isEmpty(list)) return;
        if (fixedTTL) {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(list), ttl, unit);
        } else {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(list), setRandomTTL(ttl), unit);
        }
    }

    /**
     * 缓存对象为hash
     */
    public <T> void setHash(String key, T obj, long ttl, TimeUnit unit, boolean fixedTTL) {
        if (obj == null) return;
        try {
            Map<String, Object> map = PropertyUtils.describe(obj);
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }
        if (fixedTTL) {
            redisTemplate.expire(key, ttl, unit);
        } else {
            redisTemplate.expire(key, setRandomTTL(ttl), unit);
        }
    }

    /**
     * 缓存map数据到hash
     */
    public <K, V> void setHash(String key, Map<K, V> map, long ttl, TimeUnit unit, boolean fixedTTL) {
        if (MapUtils.isEmpty(map)) return;
        redisTemplate.opsForHash().putAll(key, map);
        if (fixedTTL) {
            redisTemplate.expire(key, ttl, unit);
        } else {
            redisTemplate.expire(key, setRandomTTL(ttl), unit);
        }
    }
    //缓存操作------------------------------------------------------------------------------------------------------------


    //获取操作------------------------------------------------------------------------------------------------------------

    /**
     * 获取缓存字符串
     */
    public String getStr(String key) {
        Object str = redisTemplate.opsForValue().get(key);
        //未命中缓存
        if (str == null) {
            return null;
        }
        return (String) str;
    }


    /**
     * 获取json转换为对象
     */
    public <T> T getObjByJson(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        //未命中缓存
        if (obj == null) {
            return null;
        }

        String json = (String) obj;
        //命中空标记
        if (StringUtils.equals(json, RedisMark.EMPTY)) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }
        }

        //命中缓存
        return JSON.parseObject(json, clazz);
    }

    /**
     * 获取json转换为对象列表
     */
    public <T> List<T> getListByJson(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        //未命中缓存
        if (obj == null) {
            return null;
        }

        String json = (String) obj;
        //命中空标记
        if (StringUtils.equals(json, RedisMark.EMPTY)) {
            return new ArrayList<>();
        }

        //命中缓存
        return JSON.parseArray(json, clazz);
    }

    /**
     * 获取hash转换为对象
     */
    public <T> T getObjByHash(String key, Class<T> clazz) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        //未命中缓存
        if (MapUtils.isEmpty(map)) {
            return null;
        }

        Map<String, Object> stringKeyMap = new HashMap<>();
        map.forEach((k, v) -> stringKeyMap.put(k.toString(), v));
        //命中空标记
        try {
            if (stringKeyMap.containsKey(RedisMark.EMPTY)) {
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }

        //命中缓存
        String json = JSON.toJSONString(stringKeyMap);
        return JSON.parseObject(json, clazz);
    }

    /**
     * 获取hash的values转换为list
     */
    public <T> List<T> getListByHashValues(String key, Class<T> clazz) {
        List<Object> list = redisTemplate.opsForHash().values(key);
        //未命中缓存
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<String> jsonList = list.stream().map(item -> (String) item).toList();
        //命中空标记
        if (jsonList.size() == 1 && StringUtils.equals(jsonList.getFirst(), RedisMark.EMPTY)) {
            return new ArrayList<>();
        }

        //命中缓存
        return jsonList.stream().map(item -> JSON.parseObject(item, clazz)).toList();
    }

    /**
     * 获取hash缓存中的某个值
     */
    public Object getHashValue(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    //获取操作------------------------------------------------------------------------------------------------------------


    //删除操作------------------------------------------------------------------------------------------------------------

    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     */
    public void delete(List<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 删除hash缓存中的某个值
     */
    public void deleteHashValue(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 批量删除hash缓存中的值
     */
    public void deleteHashValues(String key, List<String> fields) {
        if (CollectionUtils.isEmpty(fields)) return;
        redisTemplate.opsForHash().delete(key, fields.toArray());
    }
    //删除操作------------------------------------------------------------------------------------------------------------


    //更新操作------------------------------------------------------------------------------------------------------------

    /**
     * 更新hash缓存中的某个值(重置过期时间)
     */
    public void updateHashOne(String key, String field, Object value, long ttl, TimeUnit unit, boolean fixedTTL) {
        redisTemplate.opsForHash().put(key, field, value);
        if (fixedTTL) {
            redisTemplate.expire(key, ttl, unit);
        } else {
            redisTemplate.expire(key, setRandomTTL(ttl), unit);
        }
    }

    /**
     * 更新hash缓存中所有值
     */
    public <T> void updateHashAll(String profileKey, T obj) {
        if (obj == null) return;
        try {
            Map<String, Object> map = PropertyUtils.describe(obj);
            redisTemplate.opsForHash().putAll(profileKey, map);
        } catch (Exception e) {
            throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
        }
    }
    //更新操作------------------------------------------------------------------------------------------------------------
}
