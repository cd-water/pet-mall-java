package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.Address;

import java.util.List;

public interface AddressMapper {

    List<Address> selectByUserId(Integer userId);

    void insert(Address address);

    void updateById(Address address);

    Address selectById(Integer id);

    void setAllNonDefault(Integer userId);

    //删除操作------------------------------------------------------------------------------------------------------------
    void deleteById(Integer id);

    void deleteByUserId(Integer userId);

    void deleteByUserIds(List<Integer> userIds);
    //删除操作------------------------------------------------------------------------------------------------------------
}




