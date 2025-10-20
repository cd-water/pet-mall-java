package com.cdwater.petmall.controller.manager.pet;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.PetType;
import com.cdwater.petmall.service.PetTypeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("managerPetTypeController")
@RequestMapping("/manager/petType")
public class
PetTypeController {

    @Resource
    private PetTypeService petTypeService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody PetType petType) {
        petTypeService.add(petType);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        petTypeService.removeOne(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/remove/batch")
    public Result removeBatch(@RequestBody List<Integer> ids) {
        petTypeService.removeBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody PetType petType) {
        petTypeService.edit(petType);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id) {
        PetType petType = petTypeService.query(id);
        return Result.success(petType);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(PetType petType,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<PetType> pageInfo = petTypeService.page(petType, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询所有类型
     */
    @GetMapping("/list")
    public Result list() {
        List<PetType> list = petTypeService.getAll();
        return Result.success(list);
    }
}
