package com.cdwater.petmall.controller.customer.user;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.Address;
import com.cdwater.petmall.service.AddressService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("customerAddressController")
@RequestMapping("customer/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    /**
     * 查询用户所有地址
     */
    @GetMapping("/list")
    public Result list() {
        List<Address> list = addressService.list();
        return Result.success(list);
    }

    /**
     * 新增地址
     */
    @PostMapping("/add")
    public Result add(@RequestBody Address address) {
        addressService.add(address);
        return Result.success();
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        addressService.removeOne(id);
        return Result.success();
    }

    /**
     * 修改地址
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody Address address) {
        addressService.edit(address);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id) {
        Address address = addressService.query(id);
        return Result.success(address);
    }
}
