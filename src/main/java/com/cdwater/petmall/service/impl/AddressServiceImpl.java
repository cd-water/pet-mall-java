package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.constants.RoleType;
import com.cdwater.petmall.common.constants.TextInfo;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.entity.Address;
import com.cdwater.petmall.mapper.AddressMapper;
import com.cdwater.petmall.service.AddressService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<Address> list() {
        //查询redis缓存
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.ADDRESS;
        List<Address> listCache = redisUtil.getListByJson(key, Address.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<Address> listDB = addressMapper.selectByUserId(userId);
        if (CollectionUtils.isNotEmpty(listDB)) {
            //回写redis缓存
            redisUtil.setJson(key, listDB, RedisMark.ADDRESS_TTL, TimeUnit.SECONDS, true);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setStr(key, RedisMark.EMPTY, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, true);
        }
        return listDB;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Address address) {
        //只允许普通用户添加自己的地址
        if (!ThreadUtil.hasPermission(address.getUserId(), RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //若作为默认地址，将其他地址设为非默认
        if (Objects.equals(address.getIsDefault(), TextInfo.IS_DEFAULT_ADDRESS)) {
            addressMapper.setAllNonDefault(address.getUserId());
        }

        //操作数据库
        addressMapper.insert(address);

        //清理缓存，保证数据一致性
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.ADDRESS;
        redisUtil.delete(key);
    }

    @Override
    public void removeOne(Integer id) {
        //操作数据库
        addressMapper.deleteById(id);

        //清理缓存，保证数据一致性
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.ADDRESS;
        redisUtil.delete(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Address address) {
        //只允许普通用户修改自己的地址
        if (!ThreadUtil.hasPermission(address.getUserId(), RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //若指定默认地址，将其他地址设为非默认
        if (Objects.equals(address.getIsDefault(), TextInfo.IS_DEFAULT_ADDRESS)) {
            addressMapper.setAllNonDefault(address.getUserId());
        }

        //操作数据库
        addressMapper.updateById(address);

        //清理缓存，保证数据一致性
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.ADDRESS;
        redisUtil.delete(key);
    }

    @Override
    public Address query(Integer id) {
        return addressMapper.selectById(id);
    }
}
