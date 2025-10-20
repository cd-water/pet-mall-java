package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.GoodsOrders;
import com.cdwater.petmall.model.dto.GoodsOrdersPageDTO;
import com.cdwater.petmall.model.vo.GoodsOrdersPageVO;
import com.cdwater.petmall.model.vo.GoodsOrdersShowVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface GoodsOrdersMapper {

    void insert(GoodsOrders goodsOrders);

    GoodsOrders selectByOrderNo(Long orderNo);

    List<GoodsOrdersPageVO> selectPage(GoodsOrdersPageDTO goodsOrdersPageDTO);

    GoodsOrdersPageVO selectById(Integer id);

    void updateById(GoodsOrders goodsOrders);

    List<GoodsOrdersShowVO> selectByUserId(Integer userId);

    List<GoodsOrders> selectByOrderStatusAndBeforeTime(Integer orderStatus, LocalDateTime time);
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




