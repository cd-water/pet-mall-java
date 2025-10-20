package com.cdwater.petmall.controller.customer.pet;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.PetPageDTO;
import com.cdwater.petmall.model.vo.PetDetailVO;
import com.cdwater.petmall.model.vo.PetShowVO;
import com.cdwater.petmall.service.PetService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("customerPetController")
@RequestMapping("/customer/pet")
public class PetController {

    @Resource
    private PetService petService;

    /**
     * 获取推荐宠物列表（共12个）
     */
    @GetMapping("/visitor/recommend")
    public Result getRecommend() {
        List<PetShowVO> list = petService.getRecommend();
        return Result.success(list);
    }

    /**
     * 顾客端分页查询
     */
    @GetMapping("/visitor/page")
    public Result page(PetPageDTO petPageDTO) {
        PageInfo<PetShowVO> pageInfo = petService.pagePublic(petPageDTO);
        return Result.success(pageInfo);
    }

    /**
     * 宠物详情
     */
    @GetMapping("/visitor/detail/{id}")
    public Result detail(@PathVariable Integer id, Integer userId, Integer role) {
        PetDetailVO petDetailVO = petService.detail(id, userId, role);
        return Result.success(petDetailVO);
    }
}
