package com.cdwater.petmall.controller.customer.user;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.Collect;
import com.cdwater.petmall.model.vo.CollectPetVO;
import com.cdwater.petmall.service.CollectService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("customerCollectController")
@RequestMapping("customer/collect")
public class CollectController {

    @Resource
    private CollectService collectService;

    /**
     * 查询用户所有收藏
     */
    @GetMapping("/list")
    public Result list() {
        List<CollectPetVO> list = collectService.list();
        return Result.success(list);
    }

    /**
     * 加入收藏夹
     */
    @PostMapping("/join")
    public Result join(@RequestBody Collect collect) {
        collectService.join(collect);
        return Result.success();
    }

    /**
     * 移出收藏夹
     */
    @DeleteMapping("/out")
    public Result out(Integer userId, Integer petId) {
        collectService.out(userId, petId);
        return Result.success();
    }
}
