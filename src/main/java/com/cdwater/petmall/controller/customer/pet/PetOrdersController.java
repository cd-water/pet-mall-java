package com.cdwater.petmall.controller.customer.pet;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.PetOrdersPlaceDTO;
import com.cdwater.petmall.model.vo.PetOrdersPlaceVO;
import com.cdwater.petmall.model.vo.PetOrdersShowVO;
import com.cdwater.petmall.service.PetOrdersService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("customerPetOrdersController")
@RequestMapping("customer/petOrders")
public class PetOrdersController {

    @Resource
    private PetOrdersService petOrdersService;

    /**
     * 用户下单
     */
    @PostMapping("/place")
    public Result placeOrder(@RequestBody PetOrdersPlaceDTO petOrdersPlaceDTO) {
        PetOrdersPlaceVO petOrdersPlaceVO = petOrdersService.placeOrder(petOrdersPlaceDTO);
        return Result.success(petOrdersPlaceVO);
    }

    /**
     * 用户取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result cancelOrder(@PathVariable Long orderNo) {
        petOrdersService.cancelOrder(orderNo);
        return Result.success();
    }

    /**
     * 用户完成订单
     */
    @PostMapping("/completed/{orderNo}")
    public Result completedOrder(@PathVariable Long orderNo) {
        petOrdersService.completedOrder(orderNo);
        return Result.success();
    }

    /**
     * 用户订单支付
     */
    @PostMapping("/payment/{orderNo}")
    public Result paymentOrder(@PathVariable Long orderNo) {
        petOrdersService.paymentOrder(orderNo);
        return Result.success();
    }

    /**
     * 查询所有订单
     */
    @GetMapping("/list")
    public Result list() {
        List<PetOrdersShowVO> list = petOrdersService.list();
        return Result.success(list);
    }
}
