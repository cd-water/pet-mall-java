package com.cdwater.petmall.controller.manager.goods;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.GoodsType;
import com.cdwater.petmall.service.GoodsTypeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("managerGoodsTypeController")
@RequestMapping("/manager/goodsType")
public class GoodsTypeController {

    @Resource
    private GoodsTypeService goodsTypeService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody GoodsType goodsType) {
        goodsTypeService.add(goodsType);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        goodsTypeService.removeOne(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/remove/batch")
    public Result removeBatch(@RequestBody List<Integer> ids) {
        goodsTypeService.removeBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody GoodsType goodsType) {
        goodsTypeService.edit(goodsType);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id) {
        GoodsType goodsType = goodsTypeService.query(id);
        return Result.success(goodsType);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(GoodsType goodsType,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<GoodsType> pageInfo = goodsTypeService.page(goodsType, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询所有类型
     */
    @GetMapping("/list")
    public Result list() {
        List<GoodsType> list = goodsTypeService.getAll();
        return Result.success(list);
    }
}