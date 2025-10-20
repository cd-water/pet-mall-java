package com.cdwater.petmall.controller.customer;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.PetShopPageDTO;
import com.cdwater.petmall.model.vo.PetShopShowVO;
import com.cdwater.petmall.model.vo.PetShopDetailVO;
import com.cdwater.petmall.service.PetShopService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("customerPetShopController")
@RequestMapping("/customer/petShop")
public class PetShopController {

    @Resource
    private PetShopService petShopService;

    /**
     * 获取热门宠物店（订单数前6名）
     */
    @GetMapping("/visitor/hot")
    public Result getHot() {
        List<PetShopShowVO> list = petShopService.getHot();
        return Result.success(list);
    }

    /**
     * 顾客端分页查询
     */
    @GetMapping("/visitor/page")
    public Result page(PetShopPageDTO petShopPageDTO) {
        PageInfo<PetShopShowVO> pageInfo = petShopService.pagePublic(petShopPageDTO);
        return Result.success(pageInfo);
    }

    /**
     * 宠物店详情
     */
    @GetMapping("/visitor/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        PetShopDetailVO petShopDetailVO = petShopService.detail(id);
        return Result.success(petShopDetailVO);
    }
}
