package com.cdwater.petmall.controller.customer.goods;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.GoodsPageDTO;
import com.cdwater.petmall.model.vo.GoodsShowVO;
import com.cdwater.petmall.service.GoodsService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("customerGoodsController")
@RequestMapping("/customer/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    /**
     * 获取热销宠物用品（销量前12名）
     */
    @GetMapping("/visitor/hot")
    public Result getHot() {
        List<GoodsShowVO> list = goodsService.getHot();
        return Result.success(list);
    }

    /**
     * 顾客端分页查询
     */
    @GetMapping("/visitor/page")
    public Result page(GoodsPageDTO goodsPageDTO) {
        PageInfo<GoodsShowVO> pageInfo = goodsService.pagePublic(goodsPageDTO);
        return Result.success(pageInfo);
    }
}
