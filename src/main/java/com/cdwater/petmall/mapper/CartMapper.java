package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.Cart;
import com.cdwater.petmall.model.vo.CartGoodsVO;

import java.util.List;

public interface CartMapper {

    void insert(Cart cart);

    Cart selectByUserIdAndGoodsId(Integer userId, Integer goodsId);

    void updateById(Cart cart);

    List<CartGoodsVO> selectByUserId(Integer userId);

    //删除操作-----------------------------------------------------------------------------------------------------------
    void deleteByUserIdAndGoodsId(Integer userId, Integer goodsId);

    void deleteByUserId(Integer userId);

    void deleteByUserIds(List<Integer> userIds);

    void deleteByGoodsId(Integer goodsId);

    void deleteByGoodsIds(List<Integer> goodsIds);
    //删除操作-----------------------------------------------------------------------------------------------------------
}




