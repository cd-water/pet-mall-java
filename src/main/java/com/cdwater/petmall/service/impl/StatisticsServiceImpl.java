package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.*;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.mapper.*;
import com.cdwater.petmall.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private PetShopMapper petShopMapper;
    @Resource
    private PetMapper petMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private PetOrdersMapper petOrdersMapper;
    @Resource
    private GoodsOrdersMapper goodsOrdersMapper;

    /**
     * 管理员端统计
     */
    @Override
    public Map<String, Object> getAdminCount() {
        Integer settledShopCount = petShopMapper.countByAuditStatus(TextInfo.AUDIT_SUCCESS);
        Integer salePetCount = petMapper.countBySaleStatus(TextInfo.ON_SALE);
        Integer saleGoodsCount = goodsMapper.countBySaleStatus(TextInfo.ON_SALE);
        BigDecimal petOrdersAmount = petOrdersMapper.sumByOrderStatus();
        BigDecimal goodsOrdersAmount = goodsOrdersMapper.sumByOrderStatus();

        Map<String, Object> map = new HashMap<>();
        map.put("settledShopCount", settledShopCount);
        map.put("salePetCount", salePetCount);
        map.put("saleGoodsCount", saleGoodsCount);
        map.put("petOrdersAmount", petOrdersAmount);
        map.put("goodsOrdersAmount", goodsOrdersAmount);
        return map;
    }

    @Override
    public List<Map<String, Object>> getPetPie() {
        return petMapper.getTypeCountMap();
    }

    @Override
    public Map<String, Object> getPetOrdersBar() {
        List<Map<String, Object>> mapList = petOrdersMapper.getShopAmountMap();

        List<String> shopNameList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();

        mapList.forEach(item -> {
            shopNameList.add((String) item.get("shopName"));
            amountList.add((BigDecimal) item.get("amount"));
        });

        Map<String, Object> map = new HashMap<>();
        map.put("shopNameList", shopNameList);
        map.put("amountList", amountList);
        return map;
    }

    @Override
    public List<Map<String, Object>> getGoodsPie() {
        return goodsMapper.getTypeCountMap();
    }

    @Override
    public Map<String, Object> getGoodsOrdersBar() {
        List<Map<String, Object>> mapList = goodsOrdersMapper.getShopAmountMap();

        List<String> shopNameList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();

        mapList.forEach(item -> {
            shopNameList.add((String) item.get("shopName"));
            amountList.add((BigDecimal) item.get("amount"));
        });

        Map<String, Object> map = new HashMap<>();
        map.put("shopNameList", shopNameList);
        map.put("amountList", amountList);
        return map;
    }


    /**
     * 宠物店端统计
     */
    @Override
    public Map<String, Object> getShopTodayData(Integer id) {
        //只允许宠物店查看本店的今日数据
        if (!ThreadUtil.hasPermission(id, RoleType.PETSHOP)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //宠物订单数据
        List<Map<String, Object>> petOrdersMapList = petOrdersMapper.selectTodayDataByShopId(id);
        BigDecimal petOrdersTurnover = BigDecimal.ZERO;
        Long todayPetOrders = 0L;
        for (Map<String, Object> item : petOrdersMapList) {
            BigDecimal price = (BigDecimal) item.get("amount");
            if (price != null && !OrderStatus.CANCELLED.equals(((Long) item.get("status")).intValue())) {
                petOrdersTurnover = petOrdersTurnover.add(price);
            }
            Long num = (Long) item.get("count");
            todayPetOrders += num;
        }

        Map<String, Object> petOrdersMap = new HashMap<>();
        petOrdersMap.put("turnover", petOrdersTurnover);
        petOrdersMap.put("todayOrders", todayPetOrders);
        petOrdersMap.put("pendingPayment", petOrdersMapList.get(OrderStatus.PENDING_PAYMENT).get("count"));
        petOrdersMap.put("pendingAccept", petOrdersMapList.get(OrderStatus.PENDING_ACCEPT).get("count"));
        petOrdersMap.put("delivering", petOrdersMapList.get(OrderStatus.DELIVERING).get("count"));
        petOrdersMap.put("delivered", petOrdersMapList.get(OrderStatus.DELIVERED).get("count"));
        petOrdersMap.put("completed", petOrdersMapList.get(OrderStatus.COMPLETED).get("count"));
        petOrdersMap.put("cancelled", petOrdersMapList.get(OrderStatus.CANCELLED).get("count"));


        //商品订单数据
        List<Map<String, Object>> goodsOrdersMapList = goodsOrdersMapper.selectTodayDataByShopId(id);
        BigDecimal goodsOrdersTurnover = BigDecimal.ZERO;
        Long todayGoodsOrders = 0L;
        for (Map<String, Object> item : goodsOrdersMapList) {
            BigDecimal price = (BigDecimal) item.get("amount");
            if (price != null && !OrderStatus.CANCELLED.equals(((Long) item.get("status")).intValue())) {
                goodsOrdersTurnover = goodsOrdersTurnover.add(price);
            }
            Long num = (Long) item.get("count");
            todayGoodsOrders += num;
        }

        Map<String, Object> goodsOrdersMap = new HashMap<>();
        goodsOrdersMap.put("turnover", goodsOrdersTurnover);
        goodsOrdersMap.put("todayOrders", todayGoodsOrders);
        goodsOrdersMap.put("pendingPayment", goodsOrdersMapList.get(OrderStatus.PENDING_PAYMENT).get("count"));
        goodsOrdersMap.put("pendingAccept", goodsOrdersMapList.get(OrderStatus.PENDING_ACCEPT).get("count"));
        goodsOrdersMap.put("delivering", goodsOrdersMapList.get(OrderStatus.DELIVERING).get("count"));
        goodsOrdersMap.put("delivered", goodsOrdersMapList.get(OrderStatus.DELIVERED).get("count"));
        goodsOrdersMap.put("completed", goodsOrdersMapList.get(OrderStatus.COMPLETED).get("count"));
        goodsOrdersMap.put("cancelled", goodsOrdersMapList.get(OrderStatus.CANCELLED).get("count"));

        //汇总统计
        Map<String, Object> map = new HashMap<>();
        map.put("turnover", ((BigDecimal) petOrdersMap.get("turnover")).add((BigDecimal) goodsOrdersMap.get("turnover")));
        map.put("todayOrders", (Long) petOrdersMap.get("todayOrders") + (Long) goodsOrdersMap.get("todayOrders"));
        map.put("pendingPayment", (Long) petOrdersMap.get("pendingPayment") + (Long) goodsOrdersMap.get("pendingPayment"));
        map.put("pendingAccept", (Long) petOrdersMap.get("pendingAccept") + (Long) goodsOrdersMap.get("pendingAccept"));
        map.put("delivering", (Long) petOrdersMap.get("delivering") + (Long) goodsOrdersMap.get("delivering"));
        map.put("delivered", (Long) petOrdersMap.get("delivered") + (Long) goodsOrdersMap.get("delivered"));
        map.put("completed", (Long) petOrdersMap.get("completed") + (Long) goodsOrdersMap.get("completed"));
        map.put("cancelled", (Long) petOrdersMap.get("cancelled") + (Long) goodsOrdersMap.get("cancelled"));
        return map;
    }

    @Override
    public Map<String, Object> getShopAmountLine(Integer id, LocalDate begin, LocalDate end) {
        //只允许宠物店查看本店的营业数据
        if (!ThreadUtil.hasPermission(id, RoleType.PETSHOP)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //计算日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //计算金额列表（有效）
        List<BigDecimal> petAmountList = petOrdersMapper.selectRangeAmountDataByShopId(id, dateList);
        List<BigDecimal> goodsAmountList = goodsOrdersMapper.selectRangeAmountDataByShopId(id, dateList);

        //合并金额列表（有效）
        List<BigDecimal> addAmountList = IntStream.range(0, petAmountList.size())
                .mapToObj(i -> {
                    BigDecimal petAmount = petAmountList.get(i) != null ? petAmountList.get(i) : BigDecimal.ZERO;
                    BigDecimal goodsAmount = goodsAmountList.get(i) != null ? goodsAmountList.get(i) : BigDecimal.ZERO;
                    return petAmount.add(goodsAmount);
                })
                .toList();

        Map<String, Object> map = new HashMap<>();
        map.put("dateList", dateList);
        map.put("amountList", addAmountList);
        return map;
    }

    @Override
    public Map<String, Object> getShopNumberLine(Integer id, LocalDate begin, LocalDate end) {
        //只允许宠物店查看本店的营业数据
        if (!ThreadUtil.hasPermission(id, RoleType.PETSHOP)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //计算日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //计算订单列表（总&有效）
        List<Map<String, Long>> petNumberList = petOrdersMapper.selectRangeNumberDataByShopId(id, dateList);
        List<Map<String, Long>> goodsNumberList = goodsOrdersMapper.selectRangeNumberDataByShopId(id, dateList);

        //合并订单列表（总&有效）
        List<Long> totalNumberList = IntStream.range(0, petNumberList.size())
                .mapToObj(i -> petNumberList.get(i).get("totalNumber") + goodsNumberList.get(i).get("totalNumber"))
                .toList();
        List<Long> validNumberList = IntStream.range(0, petNumberList.size())
                .mapToObj(i -> petNumberList.get(i).get("validNumber") + goodsNumberList.get(i).get("validNumber"))
                .toList();

        Map<String, Object> map = new HashMap<>();
        map.put("dateList", dateList);
        map.put("totalNumberList", totalNumberList);
        map.put("validNumberList", validNumberList);
        return map;
    }
}
