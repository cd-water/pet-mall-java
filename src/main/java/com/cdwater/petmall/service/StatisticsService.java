package com.cdwater.petmall.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    /**
     * 管理员端统计
     */
    Map<String, Object> getAdminCount();

    List<Map<String, Object>> getPetPie();

    Map<String, Object> getPetOrdersBar();

    List<Map<String, Object>> getGoodsPie();

    Map<String, Object> getGoodsOrdersBar();


    /**
     * 宠物店端统计
     */
    Map<String, Object> getShopTodayData(Integer id);

    Map<String, Object> getShopAmountLine(Integer id, LocalDate begin, LocalDate end);

    Map<String, Object> getShopNumberLine(Integer id, LocalDate begin, LocalDate end);
}
