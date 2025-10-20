package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.Admin;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminService {
    void add(Admin admin);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(Admin admin,HttpServletRequest request);

    Admin query(Integer id, HttpServletRequest request);

    PageInfo<Admin> page(Admin admin, Integer pageNum, Integer pageSize);
}
