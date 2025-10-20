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
import com.cdwater.petmall.entity.PetOrders;
import com.cdwater.petmall.entity.User;
import com.cdwater.petmall.mapper.AddressMapper;
import com.cdwater.petmall.mapper.PetMapper;
import com.cdwater.petmall.mapper.PetOrdersMapper;
import com.cdwater.petmall.mapper.UserMapper;
import com.cdwater.petmall.model.dto.*;
import com.cdwater.petmall.model.vo.*;
import com.cdwater.petmall.service.PetOrdersService;
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
public class PetOrdersServiceImpl implements PetOrdersService {

    @Resource
    private PetOrdersMapper petOrdersMapper;
    @Resource
    private PetMapper petMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrderNoUtil orderNoUtil;
    @Resource
    private WebSocketServer webSocketServer;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public PageInfo<PetOrdersPageVO> page(PetOrdersPageDTO petOrdersPageDTO) {
        if (RoleType.isPetShop(ThreadUtil.getRole())) {
            //宠物店端查看本店的订单
            petOrdersPageDTO.setShopId(ThreadUtil.getId());
        }

        //管理员查看所有的订单
        PageHelper.startPage(petOrdersPageDTO.getPageNum(), petOrdersPageDTO.getPageSize());
        List<PetOrdersPageVO> list = petOrdersMapper.selectPage(petOrdersPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public void acceptOrder(Long orderNo) {
        //校验订单是否存在
        PetOrders petOrdersDB = petOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (petOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //修改订单状态->派送中
        PetOrders petOrders = PetOrders.builder()
                .id(petOrdersDB.getId())
                .orderStatus(OrderStatus.DELIVERING)
                .build();
        petOrdersMapper.updateById(petOrders);
    }

    @Override
    public void deliveryOrder(Long orderNo) {
        //校验订单是否存在
        PetOrders petOrdersDB = petOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (petOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //修改订单状态->已送达
        PetOrders petOrders = PetOrders.builder()
                .id(petOrdersDB.getId())
                .orderStatus(OrderStatus.DELIVERED)
                .build();
        petOrdersMapper.updateById(petOrders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(Long orderNo) {
        //校验订单是否存在
        PetOrders petOrdersDB = petOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (petOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //模拟退款（此处退款到用户余额，后续可扩展为原路退款）
        userMapper.addBalance(petOrdersDB.getUserId(), petOrdersDB.getPetPrice());
        //库存加一
        petMapper.addStock(petOrdersDB.getPetId());

        //清理用户余额缓存，保证数据一致性
        String balanceKey = RedisMark.USER + ":" + petOrdersDB.getUserId() + ":" + RedisMark.BALANCE;
        redisUtil.delete(balanceKey);

        //修改订单状态->已取消
        PetOrders petOrders = PetOrders.builder()
                .id(petOrdersDB.getId())
                .orderStatus(OrderStatus.CANCELLED)
                .build();
        petOrdersMapper.updateById(petOrders);
    }

    @Override
    public PetOrdersPlaceVO placeOrder(PetOrdersPlaceDTO petOrdersPlaceDTO) {
        //校验权限，保证本人操作
        if (!ThreadUtil.hasPermission(petOrdersPlaceDTO.getUserId(), RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //生成订单号
        long orderNo = orderNoUtil.nextId(RedisMark.PET_ORDERS);
        //设置订单状态为待付款
        Integer orderStatus = OrderStatus.PENDING_PAYMENT;
        //设置下单时间
        LocalDateTime orderTime = LocalDateTime.now();

        //查询宠物信息
        PetQueryVO petQueryVO = petMapper.selectById(petOrdersPlaceDTO.getPetId());
        if (petQueryVO == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }
        String petName = petQueryVO.getName();
        String petImg = petQueryVO.getImg();
        BigDecimal petPrice = petQueryVO.getPrice();

        //查询地址信息
        Address address = addressMapper.selectById(petOrdersPlaceDTO.getAddressId());
        if (address == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }
        String consignee = address.getConsignee();
        String phoneNumber = address.getPhoneNumber();
        String provinceCode = address.getProvinceCode();
        String cityCode = address.getCityCode();
        String districtCode = address.getDistrictCode();
        String detailAddress = address.getDetailAddress();

        //封装订单信息
        PetOrders petOrders = PetOrders.builder()
                .orderNo(orderNo)
                .orderStatus(orderStatus)
                .orderTime(orderTime)
                .userId(petOrdersPlaceDTO.getUserId())
                .shopId(petOrdersPlaceDTO.getShopId())
                .petId(petOrdersPlaceDTO.getPetId())
                .petName(petName)
                .petImg(petImg)
                .petPrice(petPrice)
                .addressId(petOrdersPlaceDTO.getAddressId())
                .consignee(consignee)
                .phoneNumber(phoneNumber)
                .provinceCode(provinceCode)
                .cityCode(cityCode)
                .districtCode(districtCode)
                .detailAddress(detailAddress)
                .build();

        //插入数据库
        petOrdersMapper.insert(petOrders);

        //封装VO返回结果
        return PetOrdersPlaceVO.builder()
                .orderNo(orderNo)
                .orderStatus(orderStatus)
                .orderTime(orderTime)
                .orderAmount(petPrice)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderNo) {
        //校验订单是否存在
        PetOrders petOrdersDB = petOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (petOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //明确操作人角色
        Integer role = ThreadUtil.getRole();

        //用户操作
        if (RoleType.isUser(role)) {
            //派送中、已送达、已完成、已取消等状态下无法取消订单
            if (petOrdersDB.getOrderStatus() >= 2) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }

            //待接单状态下取消订单需要进行退款
            if (Objects.equals(petOrdersDB.getOrderStatus(), OrderStatus.PENDING_ACCEPT)) {
                //模拟退款（此处退款到用户余额，后续可扩展为原路退款）
                userMapper.addBalance(petOrdersDB.getUserId(), petOrdersDB.getPetPrice());
                //库存加一
                petMapper.addStock(petOrdersDB.getPetId());

                //清理用户余额缓存，保证数据一致性
                String balanceKey = RedisMark.USER + ":" + petOrdersDB.getUserId() + ":" + RedisMark.BALANCE;
                redisUtil.delete(balanceKey);
            }
        } else if (RoleType.isPetShop(role)) {
            //宠物店操作
            //已送达、已完成、已取消状态下无法取消订单
            if (petOrdersDB.getOrderStatus() >= 3) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }

            //待接单、派送中状态下取消订单需要进行退款
            if (Objects.equals(petOrdersDB.getOrderStatus(), OrderStatus.PENDING_ACCEPT) ||
                    Objects.equals(petOrdersDB.getOrderStatus(), OrderStatus.DELIVERING)) {
                //模拟退款（此处退款到用户余额，后续可扩展为原路退款）
                userMapper.addBalance(petOrdersDB.getUserId(), petOrdersDB.getPetPrice());
                //库存加一
                petMapper.addStock(petOrdersDB.getPetId());

                //清理用户余额缓存，保证数据一致性
                String balanceKey = RedisMark.USER + ":" + petOrdersDB.getUserId() + ":" + RedisMark.BALANCE;
                redisUtil.delete(balanceKey);
            }
        } else {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //修改订单状态->已取消
        PetOrders petOrders = PetOrders.builder()
                .id(petOrdersDB.getId())
                .orderStatus(OrderStatus.CANCELLED)
                .build();
        petOrdersMapper.updateById(petOrders);
    }

    @Override
    public void completedOrder(Long orderNo) {
        //校验订单是否存在
        PetOrders petOrdersDB = petOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (petOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        //修改订单状态->已送达
        PetOrders petOrders = PetOrders.builder()
                .id(petOrdersDB.getId())
                .orderStatus(OrderStatus.COMPLETED)
                .build();
        petOrdersMapper.updateById(petOrders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paymentOrder(Long orderNo) {
        //校验订单是否存在
        PetOrders petOrdersDB = petOrdersMapper.selectByOrderNo(orderNo);

        //订单不存在
        if (petOrdersDB == null) {
            throw new BusinessException(ReturnMsg.NOT_FOUND);
        }

        User userDB = userMapper.selectById(petOrdersDB.getUserId());
        BigDecimal userBalance = userDB.getBalance();
        BigDecimal orderAmount = petOrdersDB.getPetPrice();
        //余额不足
        if (userBalance.compareTo(orderAmount) < 0) {
            throw new BusinessException(ReturnMsg.INSUFFICIENT_BALANCE);
        }

        PetQueryVO petQueryVODB = petMapper.selectById(petOrdersDB.getPetId());
        //库存不足
        if (petQueryVODB.getStore() <= 0) {
            throw new BusinessException(ReturnMsg.STOCK_NOT_ENOUGH);
        }

        //模拟扣款（此处扣除用户余额，后续可扩展为支付）
        userMapper.subBalance(petOrdersDB.getUserId(), petOrdersDB.getPetPrice());
        //宠物库存减一
        petMapper.subStock(petOrdersDB.getPetId());

        //清理用户余额缓存，保证数据一致性
        String balanceKey = RedisMark.USER + ":" + petOrdersDB.getUserId() + ":" + RedisMark.BALANCE;
        redisUtil.delete(balanceKey);

        //修改订单状态->待接单
        PetOrders petOrders = PetOrders.builder()
                .id(petOrdersDB.getId())
                .orderStatus(OrderStatus.PENDING_ACCEPT)
                .build();
        petOrdersMapper.updateById(petOrders);

        //通过WebSocket向宠物店推送待接单消息
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("type", "pet-order");
        messageMap.put("orderNo", orderNo.toString());
        messageMap.put("orderTime", petOrdersDB.getOrderTime());
        String messageJson = JSON.toJSONString(messageMap);
        webSocketServer.sendToClient(messageJson, petOrdersDB.getShopId().toString());
    }

    @Override
    public List<PetOrdersShowVO> list() {
        //非用户访问
        if (!RoleType.isUser(ThreadUtil.getRole())) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //获取当前用户id
        Integer userId = ThreadUtil.getId();

        return petOrdersMapper.selectByUserId(userId);
    }

    @Override
    public Integer count(Integer shopId) {
        //要求本店访问
        if (!ThreadUtil.hasPermission(shopId, RoleType.PETSHOP)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //统计待接单订单数
        Integer orderStatus = OrderStatus.PENDING_ACCEPT;
        return petOrdersMapper.countByShopIdAndOrderStatus(shopId, orderStatus);
    }
}
