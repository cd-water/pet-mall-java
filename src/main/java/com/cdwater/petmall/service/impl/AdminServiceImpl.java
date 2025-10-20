package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.*;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.config.properties.JwtProperties;
import com.cdwater.petmall.mapper.AdminMapper;
import com.cdwater.petmall.entity.Admin;
import com.cdwater.petmall.service.AdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private JwtProperties jwtProperties;

    @Override
    public void add(Admin admin) {
        //查询账号是否存在，账号已存在, 则抛出异常
        Admin adminDBUsername = adminMapper.selectByUsername(admin.getUsername());
        if (adminDBUsername != null) {
            throw new BusinessException(ReturnMsg.ACCOUNT_REGISTER);
        }
        //查询手机号是否存在，手机号已存在，则抛出异常
        Admin adminDBPhone = adminMapper.selectByPhone(admin.getPhone());
        if (adminDBPhone != null) {
            throw new BusinessException(ReturnMsg.PHONE_REGISTER);
        }

        //明确角色
        admin.setRole(RoleType.ADMIN);
        //默认密码md5加密存储
        admin.setPassword(DigestUtils.md5DigestAsHex(TextInfo.DEFAULT_PASSWORD.getBytes()));

        //操作数据库
        adminMapper.insert(admin);
    }

    @Override
    public void removeOne(Integer id) {
        adminMapper.deleteById(id);
    }

    @Override
    public void removeBatch(List<Integer> ids) {
        adminMapper.deleteByIds(ids);
    }

    @Override
    public void edit(Admin admin, HttpServletRequest request) {
        //查询手机号是否存在，手机号已存在，则抛出异常
        Admin adminDB = adminMapper.selectByPhone(admin.getPhone());
        if (adminDB != null && !StringUtils.equals(admin.getUsername(), adminDB.getUsername())) {
            throw new BusinessException(ReturnMsg.PHONE_REGISTER);
        }

        //操作数据库
        adminMapper.updateById(admin);

        //判断是否在修改自身信息，是则更新redis缓存
        if (ThreadUtil.hasPermission(admin.getId(), RoleType.ADMIN)) {
            String token = request.getHeader(jwtProperties.getTokenName());
            String profileKey = RedisMark.PROFILE_PREFIX + token;
            redisUtil.updateHashAll(profileKey, admin);
        }
    }

    @Override
    public Admin query(Integer id, HttpServletRequest request) {
        //判断是否在查询自身信息，是则查询redis缓存
        if (ThreadUtil.hasPermission(id, RoleType.ADMIN)) {
            String token = request.getHeader(jwtProperties.getTokenName());
            String profileKey = RedisMark.PROFILE_PREFIX + token;
            Admin adminCache = redisUtil.getObjByHash(profileKey, Admin.class);
            if (adminCache != null) {
                return adminCache;
            }
        }

        //查询非自身信息，查询数据库
        Admin admin = adminMapper.selectById(id);
        //不回显密码
        admin.setPassword(null);
        return admin;
    }

    @Override
    public PageInfo<Admin> page(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectPage(admin);
        return PageInfo.of(list);
    }
}
