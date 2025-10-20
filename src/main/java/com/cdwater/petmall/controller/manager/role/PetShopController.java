package com.cdwater.petmall.controller.manager.role;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.PetShop;
import com.cdwater.petmall.model.dto.PetShopPageDTO;
import com.cdwater.petmall.service.PetShopService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("managerPetShopController")
@RequestMapping("/manager/petShop")
public class PetShopController {

    @Resource
    private PetShopService petShopService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody PetShop petShop) {
        petShopService.add(petShop);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        petShopService.removeOne(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/remove/batch")
    public Result removeBatch(@RequestBody List<Integer> ids) {
        petShopService.removeBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody PetShop petShop, HttpServletRequest request) {
        petShopService.edit(petShop, request);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id, HttpServletRequest request) {
        PetShop petShop = petShopService.query(id, request);
        return Result.success(petShop);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(PetShopPageDTO petShopPageDTO) {
        PageInfo<PetShop> pageInfo = petShopService.page(petShopPageDTO);
        return Result.success(pageInfo);
    }
}
