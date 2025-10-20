package com.cdwater.petmall.controller.manager.pet;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.Pet;
import com.cdwater.petmall.model.dto.PetPageDTO;
import com.cdwater.petmall.model.vo.PetGroupVO;
import com.cdwater.petmall.model.vo.PetPageVO;
import com.cdwater.petmall.model.vo.PetQueryVO;
import com.cdwater.petmall.service.PetService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("managerPetController")
@RequestMapping("/manager/pet")
public class PetController {

    @Resource
    private PetService petService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Pet pet) {
        petService.add(pet);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        petService.removeOne(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/remove/batch")
    public Result removeBatch(@RequestBody List<Integer> ids) {
        petService.removeBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody Pet pet) {
        petService.edit(pet);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id) {
        PetQueryVO petQueryVO = petService.query(id);
        return Result.success(petQueryVO);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(PetPageDTO petPageDTO) {
        PageInfo<PetPageVO> pageInfo = petService.page(petPageDTO);
        return Result.success(pageInfo);
    }

    /**
     * 宠物分组查询
     */
    @GetMapping("/group")
    public Result petGroup() {
        List<PetGroupVO> list = petService.petGroup();
        return Result.success(list);
    }
}
