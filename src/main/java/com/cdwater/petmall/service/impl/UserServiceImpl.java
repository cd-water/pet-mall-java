package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.*;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.config.properties.JwtProperties;
import com.cdwater.petmall.mapper.AddressMapper;
import com.cdwater.petmall.mapper.CartMapper;
import com.cdwater.petmall.mapper.CollectMapper;
import com.cdwater.petmall.mapper.UserMapper;
import com.cdwater.petmall.entity.User;
import com.cdwater.petmall.model.dto.TopupBody;
import com.cdwater.petmall.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private CollectMapper collectMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private JwtProperties jwtProperties;

    @Override
    public void add(User user) {
        //查询账号是否存在，账号已存在, 则抛出异常
        User userDBUsername = userMapper.selectByUsername(user.getUsername());
        if (userDBUsername != null) {
            throw new BusinessException(ReturnMsg.ACCOUNT_REGISTER);
        }
        //查询手机号是否存在，手机号已存在，则抛出异常
        User userDBPhone = userMapper.selectByPhone(user.getPhone());
        if (userDBPhone != null) {
            throw new BusinessException(ReturnMsg.PHONE_REGISTER);
        }

        //完善账号信息
        user.setRole(RoleType.USER);
        //默认密码md5加密存储
        user.setPassword(DigestUtils.md5DigestAsHex(TextInfo.DEFAULT_PASSWORD.getBytes()));

        //操作数据库
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOne(Integer id) {
        //操作数据库
        cartMapper.deleteByUserId(id);//清空用户的购物车
        collectMapper.deleteByUserId(id);//清空用户的收藏夹
        addressMapper.deleteByUserId(id);//清空用户的地址簿
        userMapper.deleteById(id);//删除用户

        //清理缓存，保证数据一致性
        String cartKey = RedisMark.USER + ":" + id + ":" + RedisMark.CART;
        String collectKey = RedisMark.USER + ":" + id + ":" + RedisMark.COLLECT;
        redisUtil.delete(cartKey);
        redisUtil.delete(collectKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<Integer> ids) {
        //操作数据库
        cartMapper.deleteByUserIds(ids);//清空用户的购物车
        collectMapper.deleteByUserIds(ids);//清空用户的收藏夹
        addressMapper.deleteByUserIds(ids);//清空用户的地址簿
        userMapper.deleteByIds(ids);//删除用户

        //清理缓存，保证数据一致性
        List<String> cartKeys = new ArrayList<>();
        List<String> collectKeys = new ArrayList<>();
        for (Integer id : ids) {
            cartKeys.add(RedisMark.USER + ":" + id + ":" + RedisMark.CART);
            collectKeys.add(RedisMark.USER + ":" + id + ":" + RedisMark.COLLECT);
        }
        redisUtil.delete(cartKeys);
        redisUtil.delete(collectKeys);
    }

    @Override
    public void edit(User user, HttpServletRequest request) {
        //查询手机号是否存在，手机号已存在，则抛出异常
        User userDB = userMapper.selectByPhone(user.getPhone());
        if (userDB != null && !StringUtils.equals(user.getUsername(), userDB.getUsername())) {
            throw new BusinessException(ReturnMsg.PHONE_REGISTER);
        }

        //操作数据库
        userMapper.updateById(user);

        //判断是否在修改自身信息，是则更新redis缓存
        if (ThreadUtil.hasPermission(user.getId(), RoleType.USER)) {
            String token = request.getHeader(jwtProperties.getTokenName());
            String profileKey = RedisMark.PROFILE_PREFIX + token;
            redisUtil.updateHashAll(profileKey, user);
        }
    }

    @Override
    public User query(Integer id, HttpServletRequest request) {
        //判断是否在查询自身信息，是则查询redis缓存
        if (ThreadUtil.hasPermission(id, RoleType.USER)) {
            String token = request.getHeader(jwtProperties.getTokenName());
            String profileKey = RedisMark.PROFILE_PREFIX + token;
            User userCache = redisUtil.getObjByHash(profileKey, User.class);
            if (userCache != null) {
                //获取最新的余额
                //查询redis缓存
                String balanceKey = RedisMark.USER + ":" + id + ":" + RedisMark.BALANCE;
                String balanceStr = redisUtil.getStr(balanceKey);

                //命中缓存
                if (balanceStr != null) {
                    userCache.setBalance(new BigDecimal(balanceStr));
                } else {
                    //未命中缓存，查询数据库
                    BigDecimal balance = userMapper.selectBalance(id);
                    userCache.setBalance(balance);
                    //回写redis缓存
                    redisUtil.setStr(balanceKey, balance.toString(), RedisMark.BALANCE_TTL, TimeUnit.SECONDS, true);
                }
                return userCache;
            }
        }

        //查询非自身信息，查询数据库
        User user = userMapper.selectById(id);
        //不回显密码
        user.setPassword(null);
        return user;
    }

    @Override
    public PageInfo<User> page(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectPage(user);
        return PageInfo.of(list);
    }

    @Override
    public void topup(TopupBody topupBody) {
        //校验用户身份，非本人操作无权限
        if (!ThreadUtil.hasPermission(topupBody.getUserId(), RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //充值
        userMapper.addBalance(topupBody.getUserId(), topupBody.getTopupAmount());

        //清理缓存，保证数据一致性
        String balanceKey = RedisMark.USER + ":" + topupBody.getUserId() + ":" + RedisMark.BALANCE;
        redisUtil.delete(balanceKey);
    }
}
