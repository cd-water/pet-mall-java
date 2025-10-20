package com.cdwater.petmall.controller.manager.goods;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.*;
import com.cdwater.petmall.model.vo.GoodsOrdersPageVO;
import com.cdwater.petmall.service.GoodsOrdersService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController("managerGoodsOrdersController")
@RequestMapping("/manager/goodsOrders")
public class GoodsOrdersController {

    @Resource
    private GoodsOrdersService goodsOrdersService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(GoodsOrdersPageDTO goodsOrdersPageDTO) {
        PageInfo<GoodsOrdersPageVO> pageInfo = goodsOrdersService.page(goodsOrdersPageDTO);
        return Result.success(pageInfo);
    }

    /**
     * 接单
     */
    @PostMapping("/accept/{orderNo}")
    public Result acceptOrder(@PathVariable Long orderNo) {
        goodsOrdersService.acceptOrder(orderNo);
        return Result.success();
    }

    /**
     * 送达
     */
    @PostMapping("/delivery/{orderNo}")
    public Result deliveryOrder(@PathVariable Long orderNo) {
        goodsOrdersService.deliveryOrder(orderNo);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result cancelOrder(@PathVariable Long orderNo) {
        goodsOrdersService.cancelOrder(orderNo);
        return Result.success();
    }

    /**
     * 售后退款
     */
    @PostMapping("/refund/{orderNo}")
    public Result refundOrder(@PathVariable Long orderNo) {
        goodsOrdersService.refundOrder(orderNo);
        return Result.success();
    }

    /**
     * 查询待接单订单数
     */
    @GetMapping("/count/{shopId}")
    public Result count(@PathVariable Integer shopId) {
        Integer count = goodsOrdersService.count(shopId);
        return Result.success(count);
    }
}
