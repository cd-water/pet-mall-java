package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.Cart;
import com.cdwater.petmall.model.vo.CartGoodsVO;

import java.util.List;

public interface CartService {
    List<CartGoodsVO> list();

    void join(Cart cart);

    void out(Integer userId, Integer goodsId);
}
