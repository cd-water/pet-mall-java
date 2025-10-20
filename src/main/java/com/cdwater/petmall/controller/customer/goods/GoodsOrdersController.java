package com.cdwater.petmall.controller.customer.goods;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.GoodsOrdersPlaceDTO;
import com.cdwater.petmall.model.vo.GoodsOrdersPlaceVO;
import com.cdwater.petmall.model.vo.GoodsOrdersShowVO;
import com.cdwater.petmall.service.GoodsOrdersService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("customerGoodsOrdersController")
@RequestMapping("customer/goodsOrders")
public class GoodsOrdersController {

    @Resource
    private GoodsOrdersService goodsOrdersService;

    /**
     * 用户下单
     */
    @PostMapping("/place")
    public Result placeOrder(@RequestBody GoodsOrdersPlaceDTO goodsOrdersPlaceDTO) {
        List<GoodsOrdersPlaceVO> list = goodsOrdersService.placeOrder(goodsOrdersPlaceDTO);
        return Result.success(list);
    }

    /**
     * 用户取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result cancelOrder(@PathVariable Long orderNo) {
        goodsOrdersService.cancelOrder(orderNo);
        return Result.success();
    }

    /**
     * 用户完成订单
     */
    @PostMapping("/completed/{orderNo}")
    public Result completedOrder(@PathVariable Long orderNo) {
        goodsOrdersService.completedOrder(orderNo);
        return Result.success();
    }

    /**
     * 用户订单支付
     */
    @PostMapping("/payment")
    public Result paymentOrder(@RequestBody List<Long> orderNoArray) {
        goodsOrdersService.paymentOrder(orderNoArray);
        return Result.success();
    }

    /**
     * 查询所有订单
     */
    @GetMapping("/list")
    public Result list() {
        List<GoodsOrdersShowVO> list = goodsOrdersService.list();
        return Result.success(list);
    }
}
