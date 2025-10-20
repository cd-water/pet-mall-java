package com.cdwater.petmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.OrderStatus;
import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.constants.RoleType;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.OrderNoUtil;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.entity.Address;
import com.cdwater.petmall.entity.GoodsOrders;
import com.cdwater.petmall.entity.User;
import com.cdwater.petmall.mapper.AddressMapper;
import com.cdwater.petmall.mapper.GoodsMapper;
import com.cdwater.petmall.mapper.GoodsOrdersMapper;
import com.cdwater.petmall.mapper.UserMapper;
import com.cdwater.petmall.model.dto.*;
import com.cdwater.petmall.model.vo.GoodsOrdersPageVO;
import com.cdwater.petmall.model.vo.GoodsOrdersPlaceVO;
import com.cdwater.petmall.model.vo.GoodsOrdersShowVO;
import com.cdwater.petmall.model.vo.GoodsQueryVO;
import com.cdwater.petmall.service.GoodsOrdersService;
import com.cdwater.petmall.websocket.WebSocketServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GoodsOrdersServiceImpl implements GoodsOrdersService {

    @Resource
    private GoodsOrdersMapper goodsOrdersMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private OrderNoUtil orderNoUtil;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private WebSocketServer webSocketServer;
    @Resource
    private RedisUtil redisUtil;


    @Override
    public PageInfo<GoodsOrdersPageVO> page(GoodsOrdersPageDTO goodsOrdersPageDTO) {
        if (RoleType.isPetShop(ThreadUtil.getRole())) {
            //宠物店端查看本店的订单
            goodsOrdersPageDTO.setShopId(ThreadUtil.getId());
        }

        //管理员查看所有的订单
        PageHelper.startPage(goodsOrdersPageDTO.getPageNum(), goodsOrdersPageDTO.getPageSize());
        List<GoodsOrdersPageVO> list = goodsOrdersMapper.selectPage(goodsOrdersPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public void acceptOrder(Long orderNo) {
        //校验订单是否存在
        GoodsOrders goodsOrdersDB = goodsOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (goodsOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //修改订单状态->派送中
        GoodsOrders goodsOrders = GoodsOrders.builder()
                .id(goodsOrdersDB.getId())
                .orderStatus(OrderStatus.DELIVERING)
                .build();
        goodsOrdersMapper.updateById(goodsOrders);
    }

    @Override
    public void deliveryOrder(Long orderNo) {
        //校验订单是否存在
        GoodsOrders goodsOrdersDB = goodsOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (goodsOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //修改订单状态->已送达
        GoodsOrders goodsOrders = GoodsOrders.builder()
                .id(goodsOrdersDB.getId())
                .orderStatus(OrderStatus.DELIVERED)
                .build();
        goodsOrdersMapper.updateById(goodsOrders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderNo) {
        //校验订单是否存在
        GoodsOrders goodsOrdersDB = goodsOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (goodsOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //明确操作人角色
        Integer role = ThreadUtil.getRole();

        //用户操作
        if (RoleType.isUser(role)) {
            //派送中、已送达、已完成、已取消等状态下无法取消订单
            if (goodsOrdersDB.getOrderStatus() >= 2) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }

            //待接单状态下取消订单需要进行退款
            if (Objects.equals(goodsOrdersDB.getOrderStatus(), OrderStatus.PENDING_ACCEPT)) {
                //模拟退款（此处退款到用户余额，后续可扩展为原路退款）
                userMapper.addBalance(goodsOrdersDB.getUserId(), goodsOrdersDB.getTotalPrice());
                //库存加回，销量减回
                goodsMapper.addStockAndSubSaleVolume(goodsOrdersDB.getGoodsId(), goodsOrdersDB.getQuantity());

                //清理用户余额缓存，保证数据一致性
                String balanceKey = RedisMark.USER + ":" + goodsOrdersDB.getUserId() + ":" + RedisMark.BALANCE;
                redisUtil.delete(balanceKey);
            }
        } else if (RoleType.isPetShop(role)) {
            //宠物店操作
            //已送达、已完成、已取消状态下无法取消订单
            if (goodsOrdersDB.getOrderStatus() >= 3) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }

            //待接单、派送中状态下取消订单需要进行退款
            if (Objects.equals(goodsOrdersDB.getOrderStatus(), OrderStatus.PENDING_ACCEPT) ||
                    Objects.equals(goodsOrdersDB.getOrderStatus(), OrderStatus.DELIVERING)) {
                //模拟退款（此处退款到用户余额，后续可扩展为原路退款）
                userMapper.addBalance(goodsOrdersDB.getUserId(), goodsOrdersDB.getTotalPrice());
                //库存加回，销量减回
                goodsMapper.addStockAndSubSaleVolume(goodsOrdersDB.getGoodsId(), goodsOrdersDB.getQuantity());

                //清理用户余额缓存，保证数据一致性
                String balanceKey = RedisMark.USER + ":" + goodsOrdersDB.getUserId() + ":" + RedisMark.BALANCE;
                redisUtil.delete(balanceKey);
            }
        } else {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //修改订单状态->已取消
        GoodsOrders goodsOrders = GoodsOrders.builder()
                .id(goodsOrdersDB.getId())
                .orderStatus(OrderStatus.CANCELLED)
                .build();
        goodsOrdersMapper.updateById(goodsOrders);
    }

    @Override
    public void completedOrder(Long orderNo) {
        //校验订单是否存在
        GoodsOrders goodsOrdersDB = goodsOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (goodsOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //修改订单状态->已送达
        GoodsOrders goodsOrders = GoodsOrders.builder()
                .id(goodsOrdersDB.getId())
                .orderStatus(OrderStatus.COMPLETED)
                .build();
        goodsOrdersMapper.updateById(goodsOrders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(Long orderNo) {
        //校验订单是否存在
        GoodsOrders goodsOrdersDB = goodsOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (goodsOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //模拟退款（此处退款到用户余额，后续可扩展为原路退款）
        userMapper.addBalance(goodsOrdersDB.getUserId(), goodsOrdersDB.getTotalPrice());
        //库存加回，销量减回
        goodsMapper.addStockAndSubSaleVolume(goodsOrdersDB.getGoodsId(), goodsOrdersDB.getQuantity());

        //清理用户余额缓存，保证数据一致性
        String balanceKey = RedisMark.USER + ":" + goodsOrdersDB.getUserId() + ":" + RedisMark.BALANCE;
        redisUtil.delete(balanceKey);

        //修改订单状态->已取消
        GoodsOrders goodsOrders = GoodsOrders.builder()
                .id(goodsOrdersDB.getId())
                .orderStatus(OrderStatus.CANCELLED)
                .build();
        goodsOrdersMapper.updateById(goodsOrders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<GoodsOrdersPlaceVO> placeOrder(GoodsOrdersPlaceDTO goodsOrdersPlaceDTO) {
        //校验权限，保证本人操作
        if (!ThreadUtil.hasPermission(goodsOrdersPlaceDTO.getUserId(), RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //查询地址信息
        Address address = addressMapper.selectById(goodsOrdersPlaceDTO.getAddressId());
        if (address == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }
        String consignee = address.getConsignee();
        String phoneNumber = address.getPhoneNumber();
        String provinceCode = address.getProvinceCode();
        String cityCode = address.getCityCode();
        String districtCode = address.getDistrictCode();
        String detailAddress = address.getDetailAddress();

        //设置订单状态为待付款
        Integer orderStatus = OrderStatus.PENDING_PAYMENT;

        //返回VO列表
        List<GoodsOrdersPlaceVO> returnList = new ArrayList<>();

        List<GoodsOrdersPlaceDTO.SelectGoodsItem> selectGoods = goodsOrdersPlaceDTO.getSelectGoods();
        for (GoodsOrdersPlaceDTO.SelectGoodsItem selectGoodsItem : selectGoods) {
            //生成订单号
            long orderNo = orderNoUtil.nextId(RedisMark.GOODS_ORDERS);

            //设置下单时间
            LocalDateTime orderTime = LocalDateTime.now();

            //查询商品信息
            GoodsQueryVO goodsQueryVO = goodsMapper.selectById(selectGoodsItem.getGoodsId());
            if (goodsQueryVO == null) {
                throw new BusinessException(ReturnMsg.NOT_FOUND);
            }
            Integer shopId = goodsQueryVO.getShopId();
            String goodsName = goodsQueryVO.getName();
            String goodsImg = goodsQueryVO.getImg();
            BigDecimal goodsPrice = goodsQueryVO.getPrice();

            //计算总价
            BigDecimal totalPrice = goodsPrice.multiply(new BigDecimal(selectGoodsItem.getQuantity()));

            //封装订单信息
            GoodsOrders goodsOrders = GoodsOrders.builder()
                    .orderNo(orderNo)
                    .orderStatus(orderStatus)
                    .orderTime(orderTime)
                    .quantity(selectGoodsItem.getQuantity())
                    .totalPrice(totalPrice)
                    .userId(goodsOrdersPlaceDTO.getUserId())
                    .shopId(shopId)
                    .goodsId(selectGoodsItem.getGoodsId())
                    .goodsName(goodsName)
                    .goodsImg(goodsImg)
                    .goodsPrice(goodsPrice)
                    .addressId(goodsOrdersPlaceDTO.getAddressId())
                    .consignee(consignee)
                    .phoneNumber(phoneNumber)
                    .provinceCode(provinceCode)
                    .cityCode(cityCode)
                    .districtCode(districtCode)
                    .detailAddress(detailAddress)
                    .build();

            //插入数据库
            goodsOrdersMapper.insert(goodsOrders);

            //封装VO返回结果
            GoodsOrdersPlaceVO goodsOrdersPlaceVO = GoodsOrdersPlaceVO.builder()
                    .orderNo(orderNo)
                    .orderStatus(orderStatus)
                    .orderTime(orderTime)
                    .orderAmount(totalPrice)
                    .build();

            returnList.add(goodsOrdersPlaceVO);
        }

        return returnList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paymentOrder(List<Long> orderNoArray) {
        //下单用户
        User user = userMapper.selectById(ThreadUtil.getId());
        BigDecimal userBalance = user.getBalance();
        BigDecimal totalAmount = BigDecimal.ZERO;

        //批量发送消息
        Map<String, List<String>> allMessage = new HashMap<>();

        for (Long orderNo : orderNoArray) {
            //校验订单是否存在
            GoodsOrders goodsOrdersDB = goodsOrdersMapper.selectByOrderNo(orderNo);

            //订单不存在
            if (goodsOrdersDB == null) {
                throw new BusinessException(ReturnMsg.NOT_FOUND);
            }

            totalAmount = totalAmount.add(goodsOrdersDB.getTotalPrice());

            GoodsQueryVO goodsQueryVODB = goodsMapper.selectById(goodsOrdersDB.getGoodsId());
            //库存不足
            if (goodsQueryVODB.getStore() < goodsOrdersDB.getQuantity()) {
                throw new BusinessException(ReturnMsg.STOCK_NOT_ENOUGH);
            }

            //减库存,并添加销量
            goodsMapper.subStockAndAddSaleVolume(goodsOrdersDB.getGoodsId(), goodsOrdersDB.getQuantity());

            //修改订单状态->待接单
            GoodsOrders goodsOrders = GoodsOrders.builder()
                    .id(goodsOrdersDB.getId())
                    .orderStatus(OrderStatus.PENDING_ACCEPT)
                    .build();
            goodsOrdersMapper.updateById(goodsOrders);

            //通过WebSocket向宠物店推送待接单消息
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("type", "goods-order");
            messageMap.put("orderNo", orderNo.toString());
            messageMap.put("orderTime", goodsOrdersDB.getOrderTime());
            String messageJson = JSON.toJSONString(messageMap);

            allMessage.computeIfAbsent(goodsOrdersDB.getShopId().toString(), k -> new ArrayList<>()).add(messageJson);
        }

        //余额不足
        if (userBalance.compareTo(totalAmount) < 0) {
            throw new BusinessException(ReturnMsg.INSUFFICIENT_BALANCE);
        }

        //模拟扣款（此处扣除用户余额，后续可扩展为支付）
        userMapper.subBalance(user.getId(), totalAmount);

        //清理用户余额缓存，保证数据一致性
        String balanceKey = RedisMark.USER + ":" + user.getId() + ":" + RedisMark.BALANCE;
        redisUtil.delete(balanceKey);

        //WebSocket批量推送待接单消息
        allMessage.forEach((shopId, messageList) -> {
            for (String message : messageList) {
                webSocketServer.sendToClient(message, shopId);
            }
        });
    }

    @Override
    public List<GoodsOrdersShowVO> list() {
        //非用户访问
        if (!RoleType.isUser(ThreadUtil.getRole())) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //获取当前用户id
        Integer userId = ThreadUtil.getId();

        return goodsOrdersMapper.selectByUserId(userId);
    }

    @Override
    public Integer count(Integer shopId) {
        //要求本店访问
        if (!ThreadUtil.hasPermission(shopId, RoleType.PETSHOP)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //统计待接单订单数
        Integer orderStatus = OrderStatus.PENDING_ACCEPT;
        return goodsOrdersMapper.countByShopIdAndOrderStatus(shopId, orderStatus);
    }
}
