package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.mapper.GoodsTypeMapper;
import com.cdwater.petmall.entity.GoodsType;
import com.cdwater.petmall.service.GoodsTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GoodsTypeServiceImpl implements GoodsTypeService {

    @Resource
    private GoodsTypeMapper goodsTypeMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void add(GoodsType goodsType) {
        //类型不能重名
        GoodsType goodsTypeDB = goodsTypeMapper.selectByName(goodsType.getName());
        if (goodsTypeDB != null) {
            //类型已存在
            throw new BusinessException(ReturnMsg.Type_EXISTED);
        }

        //操作数据库
        goodsTypeMapper.insert(goodsType);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.GOODS_TYPE_HOT_KEY);
        keys.add(RedisMark.GOODS_TYPE_ALL_KEY);

        redisUtil.delete(keys);
    }

    @Override
    public void removeOne(Integer id) {
        //操作数据库
        goodsTypeMapper.deleteById(id);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.GOODS_TYPE_HOT_KEY);
        keys.add(RedisMark.GOODS_TYPE_ALL_KEY);
        redisUtil.delete(keys);
    }

    @Override
    public void removeBatch(List<Integer> ids) {
        //操作数据库
        goodsTypeMapper.deleteByIds(ids);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.GOODS_TYPE_HOT_KEY);
        keys.add(RedisMark.GOODS_TYPE_ALL_KEY);
        redisUtil.delete(keys);
    }

    @Override
    public void edit(GoodsType goodsType) {
        //类型不能重名
        GoodsType goodsTypeDB = goodsTypeMapper.selectByName(goodsType.getName());
        if (goodsTypeDB != null) {
            //类型已存在
            throw new BusinessException(ReturnMsg.Type_EXISTED);
        }

        //操作数据库
        goodsTypeMapper.updateById(goodsType);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        keys.add(RedisMark.GOODS_TYPE_HOT_KEY);
        keys.add(RedisMark.GOODS_TYPE_ALL_KEY);
        redisUtil.delete(keys);
    }

    @Override
    public GoodsType query(Integer id) {
        return goodsTypeMapper.selectById(id);
    }

    @Override
    public PageInfo<GoodsType> page(GoodsType goodsType, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GoodsType> list = goodsTypeMapper.selectPage(goodsType);
        return PageInfo.of(list);
    }

    @Override
    public List<GoodsType> getAll() {
        //查询redis缓存
        String key = RedisMark.GOODS_TYPE_ALL_KEY;
        List<GoodsType> listCache = redisUtil.getListByJson(key, GoodsType.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<GoodsType> listDB = goodsTypeMapper.selectAll();
        if (CollectionUtils.isNotEmpty(listDB)) {
            //回写redis缓存
            redisUtil.setJson(key, listDB, RedisMark.GOODS_TYPE_ALL_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setStr(key, RedisMark.EMPTY, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }

    @Override
    public List<GoodsType> getHot() {
        //查询redis缓存
        String key = RedisMark.GOODS_TYPE_HOT_KEY;
        List<GoodsType> listCache = redisUtil.getListByJson(key, GoodsType.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<GoodsType> listDB = goodsTypeMapper.selectHot4();
        if (CollectionUtils.isNotEmpty(listDB)) {
            //回写redis缓存
            redisUtil.setJson(key, listDB, RedisMark.GOODS_TYPE_HOT_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setStr(key, RedisMark.EMPTY, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }
}
