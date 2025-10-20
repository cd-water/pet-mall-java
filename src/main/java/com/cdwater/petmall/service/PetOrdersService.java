package com.cdwater.petmall.service;

import com.cdwater.petmall.model.dto.*;
import com.cdwater.petmall.model.vo.PetOrdersPlaceVO;
import com.cdwater.petmall.model.vo.PetOrdersShowVO;
import com.cdwater.petmall.model.vo.PetOrdersPageVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface PetOrdersService {
    PageInfo<PetOrdersPageVO> page(PetOrdersPageDTO petOrdersPageDTO);

    void acceptOrder(Long orderNo);

    void deliveryOrder(Long orderNo);

    void cancelOrder(Long orderNo);

    void completedOrder(Long orderNo);

    void refundOrder(Long orderNo);

    PetOrdersPlaceVO placeOrder(PetOrdersPlaceDTO petOrdersPlaceDTO);

    void paymentOrder(Long orderNo);

    List<PetOrdersShowVO> list();

    Integer count(Integer shopId);
}
