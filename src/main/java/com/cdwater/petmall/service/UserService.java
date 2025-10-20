package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.User;
import com.cdwater.petmall.model.dto.TopupBody;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    void add(User user);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(User user, HttpServletRequest request);

    User query(Integer id, HttpServletRequest request);

    PageInfo<User> page(User user, Integer pageNum, Integer pageSize);

    void topup(TopupBody topupBody);
}
