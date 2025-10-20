package com.cdwater.petmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.constants.RoleType;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.entity.Goods;
import com.cdwater.petmall.mapper.CartMapper;
import com.cdwater.petmall.mapper.GoodsMapper;
import com.cdwater.petmall.model.dto.GoodsPageDTO;
import com.cdwater.petmall.model.vo.GoodsPageVO;
import com.cdwater.petmall.model.vo.GoodsQueryVO;
import com.cdwater.petmall.model.vo.GoodsShowVO;
import com.cdwater.petmall.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void add(Goods goods) {
        //设置商品所属宠物店
        goods.setShopId(ThreadUtil.getId());
        goodsMapper.insert(goods);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOne(Integer id) {
        //操作数据库
        goodsMapper.deleteById(id);
        cartMapper.deleteByGoodsId(id);

        //判断是否删除到热门商品
        List<GoodsShowVO> listCache = redisUtil.getListByHashValues(RedisMark.GOODS_HOT_KEY, GoodsShowVO.class);
        if (listCache != null) {
            //删除到热门商品，则清理缓存
            for (GoodsShowVO item : listCache) {
                if (item.getId().equals(id)) {
                    redisUtil.delete(RedisMark.GOODS_HOT_KEY);
                    break;
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<Integer> ids) {
        //操作数据库
        goodsMapper.deleteByIds(ids);
        cartMapper.deleteByGoodsIds(ids);

        //判断是否删除到热门商品
        List<GoodsShowVO> listCache = redisUtil.getListByHashValues(RedisMark.GOODS_HOT_KEY, GoodsShowVO.class);
        if (listCache != null) {
            Set<Integer> idsSet = new HashSet<>(ids);
            //删除到热门商品，则清理缓存
            for (GoodsShowVO item : listCache) {
                if (idsSet.contains(item.getId())) {
                    redisUtil.delete(RedisMark.GOODS_HOT_KEY);
                    break;
                }
            }
        }
    }

    @Override
    public void edit(Goods goods) {
        //操作数据库
        goodsMapper.updateById(goods);

        //判断是否修改到热门商品
        List<GoodsShowVO> listCache = redisUtil.getListByHashValues(RedisMark.GOODS_HOT_KEY, GoodsShowVO.class);
        if (listCache != null) {
            //修改到热门商品，则清理缓存
            listCache.forEach(item -> {
                if (item.getId().equals(goods.getId())) {
                    redisUtil.delete(RedisMark.GOODS_HOT_KEY);
                }
            });
        }
    }

    @Override
    public GoodsQueryVO query(Integer id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public PageInfo<GoodsPageVO> page(GoodsPageDTO goodsPageDTO) {
        //是否限定宠物店范围
        if (RoleType.isPetShop(ThreadUtil.getRole())) {
            goodsPageDTO.setShopId(ThreadUtil.getId());
        }
        PageHelper.startPage(goodsPageDTO.getPageNum(), goodsPageDTO.getPageSize());
        List<GoodsPageVO> list = goodsMapper.selectPage(goodsPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public List<GoodsShowVO> getHot() {
        //查询redis缓存
        String key = RedisMark.GOODS_HOT_KEY;
        List<GoodsShowVO> listCache = redisUtil.getListByHashValues(key, GoodsShowVO.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<GoodsShowVO> listDB = goodsMapper.selectHot12();
        if (CollectionUtils.isNotEmpty(listDB)) {
            //回写redis缓存
            Map<String, String> mapSave = new HashMap<>();
            listDB.forEach(item -> mapSave.put(item.getId().toString(), JSON.toJSONString(item)));
            redisUtil.setHash(key, mapSave, RedisMark.GOODS_HOT_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }

    @Override
    public PageInfo<GoodsShowVO> pagePublic(GoodsPageDTO goodsPageDTO) {
        PageHelper.startPage(goodsPageDTO.getPageNum(), goodsPageDTO.getPageSize());
        List<GoodsShowVO> list = goodsMapper.selectPagePublic(goodsPageDTO);
        return PageInfo.of(list);
    }
}
