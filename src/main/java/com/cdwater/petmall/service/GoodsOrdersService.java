package com.cdwater.petmall.service;

import com.cdwater.petmall.model.dto.*;
import com.cdwater.petmall.model.vo.GoodsOrdersPageVO;
import com.cdwater.petmall.model.vo.GoodsOrdersPlaceVO;
import com.cdwater.petmall.model.vo.GoodsOrdersShowVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface GoodsOrdersService {
    PageInfo<GoodsOrdersPageVO> page(GoodsOrdersPageDTO goodsOrdersPageDTO);

    void acceptOrder(Long orderNo);

    void deliveryOrder(Long orderNo);

    void cancelOrder(Long orderNo);

    void completedOrder(Long orderNo);

    void refundOrder(Long orderNo);

    List<GoodsOrdersPlaceVO> placeOrder(GoodsOrdersPlaceDTO goodsOrdersPlaceDTO);

    void paymentOrder(List<Long> orderNoArray);

    List<GoodsOrdersShowVO> list();

    Integer count(Integer shopId);
}
