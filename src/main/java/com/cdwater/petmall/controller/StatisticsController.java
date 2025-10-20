package com.cdwater.petmall.controller;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.service.*;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 管理员端统计
     */
    @GetMapping("/admin/count")
    public Result getAdminCount() {
        Map<String, Object> map = statisticsService.getAdminCount();
        return Result.success(map);
    }

    @GetMapping("/admin/petPie")
    public Result getPetPie() {
        List<Map<String, Object>> petPie = statisticsService.getPetPie();
        return Result.success(petPie);
    }


    @GetMapping("/admin/petOrdersBar")
    public Result getPetOrdersBar() {
        Map<String, Object> map = statisticsService.getPetOrdersBar();
        return Result.success(map);
    }


    @GetMapping("/admin/goodsPie")
    public Result getGoodsPie() {
        List<Map<String, Object>> goodsPie = statisticsService.getGoodsPie();
        return Result.success(goodsPie);
    }

    @GetMapping("/admin/goodsOrdersBar")
    public Result getGoodsOrdersBar() {
        Map<String, Object> map = statisticsService.getGoodsOrdersBar();
        return Result.success(map);
    }


    /**
     * 宠物店端统计
     */
    @GetMapping("/shop/today/{id}")
    public Result getShopTodayData(@PathVariable Integer id) {
        Map<String, Object> map = statisticsService.getShopTodayData(id);
        return Result.success(map);
    }

    @GetMapping("/shop/amountLine/{id}")
    public Result getShopAmountLine(
            @PathVariable Integer id,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        Map<String, Object> map = statisticsService.getShopAmountLine(id, begin, end);
        return Result.success(map);
    }

    @GetMapping("/shop/numberLine/{id}")
    public Result getShopNumberLine(
            @PathVariable Integer id,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        Map<String, Object> map = statisticsService.getShopNumberLine(id, begin, end);
        return Result.success(map);
    }
}
