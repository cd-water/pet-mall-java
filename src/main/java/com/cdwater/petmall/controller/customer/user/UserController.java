package com.cdwater.petmall.controller.customer.user;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.User;
import com.cdwater.petmall.model.dto.TopupBody;
import com.cdwater.petmall.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController("customerUserController")
@RequestMapping("/customer/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 编辑个人信息
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody User user, HttpServletRequest request) {
        userService.edit(user, request);
        return Result.success();
    }

    /**
     * 查询个人信息
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id, HttpServletRequest request) {
        User user = userService.query(id, request);
        return Result.success(user);
    }

    /**
     * 用户充值
     */
    @PostMapping("/topup")
    public Result topup(@RequestBody TopupBody topupBody) {
        userService.topup(topupBody);
        return Result.success();
    }
}
