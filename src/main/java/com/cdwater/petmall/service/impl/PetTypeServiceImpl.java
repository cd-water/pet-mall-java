package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.mapper.PetTypeMapper;
import com.cdwater.petmall.entity.PetType;
import com.cdwater.petmall.service.PetTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class PetTypeServiceImpl implements PetTypeService {

    @Resource
    private PetTypeMapper petTypeMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void add(PetType petType) {
        //类型不能重名
        PetType petTypeDB = petTypeMapper.selectByName(petType.getName());
        if (petTypeDB != null) {
            //类型已存在
            throw new BusinessException(ReturnMsg.Type_EXISTED);
        }

        //操作数据库
        petTypeMapper.insert(petType);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.PET_TYPE_HOT_KEY);
        keys.add(RedisMark.PET_TYPE_ALL_KEY);
        redisUtil.delete(keys);
    }

    @Override
    public void removeOne(Integer id) {
        //操作数据库
        petTypeMapper.deleteById(id);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.PET_TYPE_HOT_KEY);
        keys.add(RedisMark.PET_TYPE_ALL_KEY);

        redisUtil.delete(keys);
    }

    @Override
    public void removeBatch(List<Integer> ids) {
        //操作数据库
        petTypeMapper.deleteByIds(ids);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.PET_TYPE_HOT_KEY);
        keys.add(RedisMark.PET_TYPE_ALL_KEY);
        redisUtil.delete(keys);
    }

    @Override
    public void edit(PetType petType) {
        //类型不能重名
        PetType petTypeDB = petTypeMapper.selectByName(petType.getName());
        if (petTypeDB != null && !Objects.equals(petTypeDB.getId(), petType.getId())) {
            //类型已存在
            throw new BusinessException(ReturnMsg.Type_EXISTED);
        }

        //操作数据库
        petTypeMapper.updateById(petType);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.PET_TYPE_HOT_KEY);
        keys.add(RedisMark.PET_TYPE_ALL_KEY);
        redisUtil.delete(keys);
    }

    @Override
    public PetType query(Integer id) {
        return petTypeMapper.selectById(id);
    }

    @Override
    public PageInfo<PetType> page(PetType petType, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PetType> list = petTypeMapper.selectPage(petType);
        return PageInfo.of(list);
    }

    @Override
    public List<PetType> getAll() {
        //查询redis缓存
        String key = RedisMark.PET_TYPE_ALL_KEY;
        List<PetType> listCache = redisUtil.getListByJson(key, PetType.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<PetType> listDB = petTypeMapper.selectAll();
        if (CollectionUtils.isNotEmpty(listDB)) {
            //回写redis缓存
            redisUtil.setJson(key, listDB, RedisMark.PET_TYPE_ALL_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setStr(key, RedisMark.EMPTY, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }

    @Override
    public List<PetType> getHot() {
        //查询redis缓存
        String key = RedisMark.PET_TYPE_HOT_KEY;
        List<PetType> listCache = redisUtil.getListByJson(key, PetType.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<PetType> listDB = petTypeMapper.selectHot4();
        if (CollectionUtils.isNotEmpty(listDB)) {
            //回写redis缓存
            redisUtil.setJson(key, listDB, RedisMark.PET_TYPE_HOT_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setStr(key, RedisMark.EMPTY, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }
}
