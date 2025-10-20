package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.PetOrders;
import com.cdwater.petmall.model.dto.PetOrdersPageDTO;
import com.cdwater.petmall.model.vo.PetOrdersShowVO;
import com.cdwater.petmall.model.vo.PetOrdersPageVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PetOrdersMapper {

    List<PetOrdersPageVO> selectPage(PetOrdersPageDTO petOrdersPageDTO);

    void insert(PetOrders petOrders);

    PetOrders selectByOrderNo(Long orderNo);

    void updateById(PetOrders petOrders);

    List<PetOrdersShowVO> selectByUserId(Integer userId);

    List<PetOrders> selectByOrderStatusAndBeforeTime(Integer orderStatus, LocalDateTime time);

    /**
     * 统计相关
     */
    BigDecimal sumByOrderStatus();

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> getShopAmountMap();

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> selectTodayDataByShopId(Integer shopId);

    List<BigDecimal> selectRangeAmountDataByShopId(Integer shopId, List<LocalDate> dateList);

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Long>> selectRangeNumberDataByShopId(Integer shopId, List<LocalDate> dateList);

    Integer countByShopIdAndOrderStatus(Integer shopId, Integer orderStatus);
}




