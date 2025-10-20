package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserMapper {

    void insert(User user);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void updateById(User user);

    User selectById(Integer id);

    List<User> selectPage(User user);

    User selectByUsername(String username);

    User selectByPhone(String phone);

    void addBalance(Integer id, BigDecimal money);

    void subBalance(Integer id, BigDecimal money);

    BigDecimal selectBalance(Integer id);
}




