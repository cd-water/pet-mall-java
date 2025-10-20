package com.cdwater.petmall.task;

import com.cdwater.petmall.common.constants.OrderStatus;
import com.cdwater.petmall.entity.GoodsOrders;
import com.cdwater.petmall.entity.PetOrders;
import com.cdwater.petmall.mapper.GoodsOrdersMapper;
import com.cdwater.petmall.mapper.PetOrdersMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Resource
    private PetOrdersMapper petOrdersMapper;
    @Resource
    private GoodsOrdersMapper goodsOrdersMapper;

    /**
     * 订单超时取消任务
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void processOrderCancelled() {
        log.info("定时处理待付款超时订单任务：{}", LocalDateTime.now());

        //处理超时10分钟的待付款订单
        LocalDateTime time = LocalDateTime.now().minusMinutes(10);
        //查询超时宠物订单和商品订单
        List<PetOrders> petOrdersList = petOrdersMapper.selectByOrderStatusAndBeforeTime(OrderStatus.PENDING_PAYMENT, time);
        List<GoodsOrders> goodsOrdersList = goodsOrdersMapper.selectByOrderStatusAndBeforeTime(OrderStatus.PENDING_PAYMENT, time);

        //修改订单状态->已取消
        if (CollectionUtils.isNotEmpty(petOrdersList)) {
            petOrdersList.forEach(item -> {
                PetOrders petOrders = PetOrders.builder()
                        .id(item.getId())
                        .orderStatus(OrderStatus.CANCELLED)
                        .build();
                petOrdersMapper.updateById(petOrders);
            });
        }
        if (CollectionUtils.isNotEmpty(goodsOrdersList)) {
            goodsOrdersList.forEach(item -> {
                GoodsOrders goodsOrders = GoodsOrders.builder()
                        .id(item.getId())
                        .orderStatus(OrderStatus.CANCELLED)
                        .build();
                goodsOrdersMapper.updateById(goodsOrders);
            });
        }
    }

    /**
     * 订单自动确认完成
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processOrderCompleted() {
        log.info("定时处理自动确定完成订单任务：{}", LocalDateTime.now());

        //处理昨天未点已完成的已送达订单
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);

        //查询为点已完成的宠物订单和商品订单
        List<PetOrders> petOrdersList = petOrdersMapper.selectByOrderStatusAndBeforeTime(OrderStatus.DELIVERED, time);
        List<GoodsOrders> goodsOrdersList = goodsOrdersMapper.selectByOrderStatusAndBeforeTime(OrderStatus.DELIVERED, time);

        //修改订单状态->已完成
        if (CollectionUtils.isNotEmpty(petOrdersList)) {
            petOrdersList.forEach(item -> {
                PetOrders petOrders = PetOrders.builder()
                        .id(item.getId())
                        .orderStatus(OrderStatus.COMPLETED)
                        .build();
                petOrdersMapper.updateById(petOrders);
            });
        }
        if (CollectionUtils.isNotEmpty(goodsOrdersList)) {
            goodsOrdersList.forEach(item -> {
                GoodsOrders goodsOrders = GoodsOrders.builder()
                        .id(item.getId())
                        .orderStatus(OrderStatus.COMPLETED)
                        .build();
                goodsOrdersMapper.updateById(goodsOrders);
            });
        }
    }
}
