package com.cdwater.petmall.controller.manager.pet;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.*;
import com.cdwater.petmall.model.vo.PetOrdersPageVO;
import com.cdwater.petmall.service.PetOrdersService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController("managerPetOrdersController")
@RequestMapping("/manager/petOrders")
public class PetOrdersController {

    @Resource
    private PetOrdersService petOrdersService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(PetOrdersPageDTO petOrdersPageDTO) {
        PageInfo<PetOrdersPageVO> pageInfo = petOrdersService.page(petOrdersPageDTO);
        return Result.success(pageInfo);
    }

    /**
     * 接单
     */
    @PostMapping("/accept/{orderNo}")
    public Result acceptOrder(@PathVariable Long orderNo) {
        petOrdersService.acceptOrder(orderNo);
        return Result.success();
    }

    /**
     * 送达
     */
    @PostMapping("/delivery/{orderNo}")
    public Result deliveryOrder(@PathVariable Long orderNo) {
        petOrdersService.deliveryOrder(orderNo);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result cancelOrder(@PathVariable Long orderNo) {
        petOrdersService.cancelOrder(orderNo);
        return Result.success();
    }

    /**
     * 售后退款
     */
    @PostMapping("/refund/{orderNo}")
    public Result refundOrder(@PathVariable Long orderNo) {
        petOrdersService.refundOrder(orderNo);
        return Result.success();
    }

    /**
     * 查询待接单订单数
     */
    @GetMapping("/count/{shopId}")
    public Result count(@PathVariable Integer shopId) {
        Integer count = petOrdersService.count(shopId);
        return Result.success(count);
    }
}
