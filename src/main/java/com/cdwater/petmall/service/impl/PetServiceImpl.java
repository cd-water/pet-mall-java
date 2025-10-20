package com.cdwater.petmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.constants.RoleType;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.entity.Collect;
import com.cdwater.petmall.entity.Pet;
import com.cdwater.petmall.mapper.CollectMapper;
import com.cdwater.petmall.mapper.PetMapper;
import com.cdwater.petmall.mapper.SlideshowMapper;
import com.cdwater.petmall.model.dto.PetPageDTO;
import com.cdwater.petmall.model.vo.*;
import com.cdwater.petmall.service.PetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PetServiceImpl implements PetService {

    @Resource
    private PetMapper petMapper;
    @Resource
    private CollectMapper collectMapper;
    @Resource
    private SlideshowMapper slideshowMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void add(Pet pet) {
        //设置宠物所属宠物店
        pet.setShopId(ThreadUtil.getId());
        petMapper.insert(pet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOne(Integer id) {
        //操作数据库
        petMapper.deleteById(id);
        collectMapper.deleteByPetId(id);
        slideshowMapper.deleteByPetId(id);

        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.PET_DETAIL_PREFIX + id);
        redisUtil.deleteHashValue(RedisMark.SLIDESHOW_KEY, id.toString());
        redisUtil.deleteHashValue(RedisMark.PET_RECOMMEND_KEY, id.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<Integer> ids) {
        //操作数据库
        petMapper.deleteByIds(ids);
        collectMapper.deleteByPetIds(ids);
        slideshowMapper.deleteByPetIds(ids);

        //清理缓存，保证数据一致性
        List<String> keys = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        for (Integer id : ids) {
            keys.add(RedisMark.PET_DETAIL_PREFIX + id);
            fields.add(id.toString());
        }
        redisUtil.delete(keys);
        redisUtil.deleteHashValues(RedisMark.SLIDESHOW_KEY, fields);
        redisUtil.deleteHashValues(RedisMark.PET_RECOMMEND_KEY, fields);
    }

    @Override
    public void edit(Pet pet) {
        //操作数据库
        petMapper.updateById(pet);

        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.PET_DETAIL_PREFIX + pet.getId());

        //如果是设置为推荐状态，清理可能存在的空标记
        if (pet.getRecommend() == 1) {
            redisUtil.deleteHashValue(RedisMark.PET_RECOMMEND_KEY, RedisMark.EMPTY);
        } else {
            redisUtil.deleteHashValue(RedisMark.PET_RECOMMEND_KEY, pet.getId().toString());
        }

        PetQueryVO petQueryVO = petMapper.selectById(pet.getId());
        //如果仍然是推荐状态，则重建缓存
        if (petQueryVO.getRecommend() == 1) {
            PetShowVO petShowVO = new PetShowVO();
            try {
                BeanUtils.copyProperties(petShowVO, petQueryVO);
            } catch (Exception e) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }
            redisUtil.updateHashOne(RedisMark.PET_RECOMMEND_KEY, pet.getId().toString(), JSON.toJSONString(petShowVO), RedisMark.PET_RECOMMEND_TTL, TimeUnit.SECONDS, false);
        }
    }

    @Override
    public PetQueryVO query(Integer id) {
        return petMapper.selectById(id);
    }

    @Override
    public PageInfo<PetPageVO> page(PetPageDTO petPageDTO) {
        //是否限定宠物店范围
        if (RoleType.isPetShop(ThreadUtil.getRole())) {
            petPageDTO.setShopId(ThreadUtil.getId());
        }
        PageHelper.startPage(petPageDTO.getPageNum(), petPageDTO.getPageSize());
        List<PetPageVO> list = petMapper.selectPage(petPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public List<PetGroupVO> petGroup() {
        return petMapper.selectPetGroup();
    }

    @Override
    public List<PetShowVO> getRecommend() {
        //查询redis缓存
        String key = RedisMark.PET_RECOMMEND_KEY;
        List<PetShowVO> listCache = redisUtil.getListByHashValues(key, PetShowVO.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<PetShowVO> listDB = petMapper.selectRecommend12();
        if (CollectionUtils.isNotEmpty(listDB)) {
            Map<String, String> mapSave = new HashMap<>();
            listDB.forEach(item -> mapSave.put(item.getId().toString(), JSON.toJSONString(item)));
            //回写redis缓存
            redisUtil.setHash(key, mapSave, RedisMark.PET_RECOMMEND_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }

    @Override
    public PageInfo<PetShowVO> pagePublic(PetPageDTO petPageDTO) {
        PageHelper.startPage(petPageDTO.getPageNum(), petPageDTO.getPageSize());
        List<PetShowVO> list = petMapper.selectPagePublic(petPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public PetDetailVO detail(Integer id, Integer userId, Integer role) {
        //查询redis缓存
        String key = RedisMark.PET_DETAIL_PREFIX + id;
        PetDetailVO petDetailVOCache = redisUtil.getObjByHash(key, PetDetailVO.class);

        //命中缓存
        if (petDetailVOCache != null) {
            //命中空标记
            if (petDetailVOCache.getId() == null) {
                return new PetDetailVO();
            }
            //命中有效值
            if (RoleType.isUser(role)) {
                //普通用户判断收藏状态
                Collect collectDB = collectMapper.selectByUserIdAndPetId(userId, id);
                petDetailVOCache.setHasCollect(collectDB != null);
            } else {
                //游客一律设置为未收藏
                petDetailVOCache.setHasCollect(false);
            }
            return petDetailVOCache;
        }

        //未命中缓存，查询数据库
        PetDetailVO petDetailVODB = petMapper.selectDetail(id);
        if (petDetailVODB != null) {
            //回写redis缓存
            redisUtil.setHash(key, petDetailVODB, RedisMark.PET_DETAIL_TTL, TimeUnit.SECONDS, false);
            if (RoleType.isUser(role)) {
                //普通用户判断收藏状态
                Collect collectDB = collectMapper.selectByUserIdAndPetId(userId, id);
                petDetailVODB.setHasCollect(collectDB != null);
            } else {
                //游客一律设置为未收藏
                petDetailVODB.setHasCollect(false);
            }
        } else {
            //数据库无数据，缓存空对象，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return petDetailVODB;
    }
}
