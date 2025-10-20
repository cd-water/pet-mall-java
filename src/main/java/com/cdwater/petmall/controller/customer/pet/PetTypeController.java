package com.cdwater.petmall.controller.customer.pet;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.PetType;
import com.cdwater.petmall.service.PetTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("customerPetTypeController")
@RequestMapping("/customer/petType")
public class PetTypeController {

    @Resource
    private PetTypeService petTypeService;

    /**
     * 查询所有类型
     */
    @GetMapping("/visitor/all")
    public Result getAll() {
        List<PetType> list = petTypeService.getAll();
        return Result.success(list);
    }

    /**
     * 查询热门类型（有效订单数前4名）
     */
    @GetMapping("/visitor/hot")
    public Result getHot() {
        List<PetType> list = petTypeService.getHot();
        return Result.success(list);
    }
}
