package com.cdwater.petmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.constants.RoleType;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.entity.Collect;
import com.cdwater.petmall.mapper.CollectMapper;
import com.cdwater.petmall.mapper.PetMapper;
import com.cdwater.petmall.model.vo.CollectPetVO;
import com.cdwater.petmall.model.vo.PetQueryVO;
import com.cdwater.petmall.service.CollectService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CollectServiceImpl implements CollectService {

    @Resource
    private CollectMapper collectMapper;
    @Resource
    private PetMapper petMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<CollectPetVO> list() {
        //查询redis缓存
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.COLLECT;
        List<CollectPetVO> listCache = redisUtil.getListByHashValues(key, CollectPetVO.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，数据库查询
        List<CollectPetVO> listDB = collectMapper.selectByUserId(userId);
        if (CollectionUtils.isNotEmpty(listDB)) {
            Map<String, String> mapSave = new HashMap<>();
            listDB.forEach(item -> mapSave.put(item.getPetId().toString(), JSON.toJSONString(item)));
            //回写redis缓存
            redisUtil.setHash(key, mapSave, RedisMark.COLLECT_TTL, TimeUnit.SECONDS, true);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, true);
        }
        return listDB;
    }

    @Override
    public void join(Collect collect) {
        //只允许普通用户添加自己的收藏夹
        if (!ThreadUtil.hasPermission(collect.getUserId(), RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //判断是否已收藏
        Collect collectDB = collectMapper.selectByUserIdAndPetId(collect.getUserId(), collect.getPetId());
        if (collectDB != null) {
            //收藏夹已存在
            throw new BusinessException(ReturnMsg.COLLECT_EXISTED);
        }

        //操作数据库
        collectMapper.insert(collect);

        //更新缓存，保证数据一致性
        Integer userId = ThreadUtil.getId();
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.COLLECT;

        //完善缓存信息
        PetQueryVO petQueryVO = petMapper.selectById(collect.getPetId());
        CollectPetVO collectPetVO = CollectPetVO.builder()
                .id(collect.getId())
                .petId(collect.getPetId())
                .petName(petQueryVO.getName())
                .petGender(petQueryVO.getGender())
                .petImg(petQueryVO.getImg())
                .petPrice(petQueryVO.getPrice())
                .petStore(petQueryVO.getStore())
                .petSaleStatus(petQueryVO.getSaleStatus())
                .build();

        redisUtil.updateHashOne(key, collect.getPetId().toString(), JSON.toJSONString(collectPetVO), RedisMark.COLLECT_TTL, TimeUnit.SECONDS, true);
        redisUtil.deleteHashValue(key, RedisMark.EMPTY);
    }

    @Override
    public void out(Integer userId, Integer petId) {
        //只允许普通用户移出自己的收藏夹
        if (!ThreadUtil.hasPermission(userId, RoleType.USER)) {
            throw new BusinessException(ReturnMsg.FORBIDDEN_ACCESS);
        }

        //操作数据库
        collectMapper.deleteByUserIdAndPetId(userId, petId);

        //更新缓存，保证数据一致性
        String key = RedisMark.USER + ":" + userId + ":" + RedisMark.COLLECT;
        redisUtil.deleteHashValue(key, petId.toString());
    }
}
