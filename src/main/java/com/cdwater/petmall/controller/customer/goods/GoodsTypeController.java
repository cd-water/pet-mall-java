package com.cdwater.petmall.controller.customer.goods;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.GoodsType;
import com.cdwater.petmall.service.GoodsTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("customerGoodsTypeController")
@RequestMapping("/customer/goodsType")
public class GoodsTypeController {

    @Resource
    private GoodsTypeService goodsTypeService;

    /**
     * 查询所有类型
     */
    @GetMapping("/visitor/all")
    public Result getAll() {
        List<GoodsType> list = goodsTypeService.getAll();
        return Result.success(list);
    }

    /**
     * 查询热门类型（订单数前4名）
     */
    @GetMapping("/visitor/hot")
    public Result getHot() {
        List<GoodsType> list = goodsTypeService.getHot();
        return Result.success(list);
    }
}
