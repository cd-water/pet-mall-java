package com.cdwater.petmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.constants.RoleType;
import com.cdwater.petmall.common.constants.TextInfo;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.entity.Cart;
import com.cdwater.petmall.mapper.CartMapper;
import com.cdwater.petmall.mapper.GoodsMapper;
import com.cdwater.petmall.model.vo.CartGoodsVO;
import com.cdwater.petmall.model.vo.GoodsQueryVO;
import com.cdwater.petmall.service.CartService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<CartGoodsVO> list() {
        //查询redis缓存
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.CART;
        List<CartGoodsVO> listCache = redisUtil.getListByHashValues(key, CartGoodsVO.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，数据库查询
        List<CartGoodsVO> listDB = cartMapper.selectByUserId(userId);
        if (CollectionUtils.isNotEmpty(listDB)) {
            Map<String, String> mapSave = new HashMap<>();
            listDB.forEach(item -> mapSave.put(item.getGoodsId().toString(), JSON.toJSONString(item)));
            //回写redis缓存
            redisUtil.setHash(key, mapSave, RedisMark.CART_TTL, TimeUnit.SECONDS, true);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, true);
        }
        return listDB;
    }

    @Override
    public void join(Cart cart) {
        //只允许普通用户添加自己的购物车
        if (!ThreadUtil.hasPermission(cart.getUserId(), RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //判断是否是第一次添加购物车
        Cart cartDB = cartMapper.selectByUserIdAndGoodsId(cart.getUserId(), cart.getGoodsId());
        if (cartDB == null) {
            //初次添加购物车，初始默认购买数量为1
            cart.setQuantity(TextInfo.INIT_CART_QUANTITY);
            cartMapper.insert(cart);
        } else {
            //购物车已存在，则数量加1
            cart.setId(cartDB.getId());
            cart.setQuantity(cartDB.getQuantity() + TextInfo.INIT_CART_QUANTITY);
            cartMapper.updateById(cart);
        }

        //更新缓存，保证数据一致性
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.CART;

        //完善缓存信息
        GoodsQueryVO goodsQueryVO = goodsMapper.selectById(cart.getGoodsId());
        CartGoodsVO cartGoodsVO = CartGoodsVO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .quantity(cart.getQuantity())
                .goodsId(cart.getGoodsId())
                .shopId(goodsQueryVO.getShopId())
                .goodsName(goodsQueryVO.getName())
                .goodsImg(goodsQueryVO.getImg())
                .goodsPrice(goodsQueryVO.getPrice())
                .goodsStore(goodsQueryVO.getStore())
                .goodsSaleStatus(goodsQueryVO.getSaleStatus())
                .build();

        redisUtil.updateHashOne(key, cart.getGoodsId().toString(), JSON.toJSONString(cartGoodsVO), RedisMark.CART_TTL, TimeUnit.SECONDS, true);
        redisUtil.deleteHashValue(key, RedisMark.EMPTY);
    }

    @Override
    public void out(Integer userId, Integer goodsId) {
        //只允许普通用户移出自己的购物车
        if (!ThreadUtil.hasPermission(userId, RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //操作数据库
        cartMapper.deleteByUserIdAndGoodsId(userId, goodsId);

        //更新缓存，保证数据一致性
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.CART;
        redisUtil.deleteHashValue(key, goodsId.toString());
    }
}
