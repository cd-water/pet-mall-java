package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.Admin;

import java.util.List;

public interface AdminMapper {

    void insert(Admin admin);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void updateById(Admin admin);

    Admin selectById(Integer id);

    List<Admin> selectPage(Admin admin);

    Admin selectByUsername(String username);

    Admin selectByPhone(String phone);
}




