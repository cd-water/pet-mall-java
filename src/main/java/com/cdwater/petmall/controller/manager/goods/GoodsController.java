package com.cdwater.petmall.controller.manager.goods;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.Goods;
import com.cdwater.petmall.model.dto.GoodsPageDTO;
import com.cdwater.petmall.model.vo.GoodsPageVO;
import com.cdwater.petmall.model.vo.GoodsQueryVO;
import com.cdwater.petmall.service.GoodsService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("managerGoodsController")
@RequestMapping("/manager/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        goodsService.add(goods);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody Goods goods) {
        goodsService.edit(goods);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        goodsService.removeOne(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/remove/batch")
    public Result removeBatch(@RequestBody List<Integer> ids) {
        goodsService.removeBatch(ids);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id) {
        GoodsQueryVO goodsQueryVO = goodsService.query(id);
        return Result.success(goodsQueryVO);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(GoodsPageDTO goodsPageDTO) {
        PageInfo<GoodsPageVO> pageInfo = goodsService.page(goodsPageDTO);
        return Result.success(pageInfo);
    }
}
