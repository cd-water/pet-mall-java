package com.cdwater.petmall.controller.customer.user;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.Cart;
import com.cdwater.petmall.model.vo.CartGoodsVO;
import com.cdwater.petmall.service.CartService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("customerCartController")
@RequestMapping("customer/cart")
public class CartController {

    @Resource
    private CartService cartService;

    /**
     * 查询用户购物车所有商品
     */
    @GetMapping("/list")
    public Result list() {
        List<CartGoodsVO> list = cartService.list();
        return Result.success(list);
    }

    /**
     * 加入购物车
     */
    @PostMapping("/join")
    public Result join(@RequestBody Cart cart) {
        cartService.join(cart);
        return Result.success();
    }

    /**
     * 移出购物车
     */
    @DeleteMapping("/out")
    public Result out(Integer userId, Integer goodsId) {
        cartService.out(userId, goodsId);
        return Result.success();
    }
}
