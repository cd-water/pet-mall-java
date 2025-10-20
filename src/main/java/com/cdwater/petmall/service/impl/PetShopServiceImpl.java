package com.cdwater.petmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.*;
import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.common.utils.ThreadUtil;
import com.cdwater.petmall.config.properties.JwtProperties;
import com.cdwater.petmall.mapper.GoodsMapper;
import com.cdwater.petmall.mapper.PetMapper;
import com.cdwater.petmall.mapper.PetShopMapper;
import com.cdwater.petmall.entity.PetShop;
import com.cdwater.petmall.mapper.SlideshowMapper;
import com.cdwater.petmall.model.dto.PetShopPageDTO;
import com.cdwater.petmall.model.vo.GoodsShowVO;
import com.cdwater.petmall.model.vo.PetShopShowVO;
import com.cdwater.petmall.model.vo.PetShopDetailVO;
import com.cdwater.petmall.service.PetShopService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PetShopServiceImpl implements PetShopService {

    @Resource
    private PetShopMapper petShopMapper;
    @Resource
    private PetMapper petMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private SlideshowMapper slideshowMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private JwtProperties jwtProperties;

    @Override
    public void add(PetShop petShop) {
        //查询账号是否存在，账号已存在, 则抛出异常
        PetShop petShopDBUsername = petShopMapper.selectByUsername(petShop.getUsername());
        if (petShopDBUsername != null) {
            throw new BusinessException(ReturnMsg.ACCOUNT_REGISTER);
        }
        //查询手机号是否存在，手机号已存在，则抛出异常
        PetShop petShopDBPhone = petShopMapper.selectByPhone(petShop.getPhone());
        if (petShopDBPhone != null) {
            throw new BusinessException(ReturnMsg.PHONE_REGISTER);
        }

        //明确角色
        petShop.setRole(RoleType.PETSHOP);
        //默认密码md5加密存储
        petShop.setPassword(DigestUtils.md5DigestAsHex(TextInfo.DEFAULT_PASSWORD.getBytes()));

        //操作数据库
        petShopMapper.insert(petShop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOne(Integer id) {
        //查询宠物店旗下所有宠物的id，用于后续删除宠物相关缓存
        List<Integer> petIds = petMapper.selectIdsByShopId(id);
        //查询宠物店旗下所有商品的id，用于后续删除商品相关缓存
        List<Integer> goodsIds = goodsMapper.selectIdsByShopId(id);

        //操作数据库
        petMapper.deleteByShopId(id);//清空宠物店下的宠物
        goodsMapper.deleteByShopId(id);//清空宠物店下的商品
        slideshowMapper.deleteByShopId(id);//删除宠物店旗下宠物的轮播图
        petShopMapper.deleteById(id);//删除宠物店

        //清理宠物店相关缓存
        redisUtil.delete(RedisMark.PET_SHOP_DETAIL_PREFIX + id);

        //判断是否删除到热门宠物店
        List<PetShopShowVO> listCache = redisUtil.getListByHashValues(RedisMark.PET_SHOP_HOT_KEY, PetShopShowVO.class);
        if (listCache != null) {
            //删除到热门宠物店，则清理缓存
            for (PetShopShowVO item : listCache) {
                if (item.getId().equals(id)) {
                    redisUtil.delete(RedisMark.PET_SHOP_HOT_KEY);
                    break;
                }
            }
        }

        //清理旗下被删除宠物详情缓存
        List<String> keys = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        for (Integer petId : petIds) {
            keys.add(RedisMark.PET_DETAIL_PREFIX + petId);
            fields.add(petId.toString());
        }
        redisUtil.delete(keys);
        redisUtil.deleteHashValues(RedisMark.PET_RECOMMEND_KEY, fields);


        //判断是否删除到热门商品
        List<GoodsShowVO> goodsListCache = redisUtil.getListByHashValues(RedisMark.GOODS_HOT_KEY, GoodsShowVO.class);
        if (goodsListCache != null) {
            Set<Integer> goodsIdsSet = new HashSet<>(goodsIds);
            //删除到热门商品，则清理缓存
            for (GoodsShowVO item : goodsListCache) {
                if (goodsIdsSet.contains(item.getId())) {
                    redisUtil.delete(RedisMark.GOODS_HOT_KEY);
                    break;
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatch(List<Integer> ids) {
        //查询所有宠物店旗下所有的宠物的id，用于后续删除宠物相关缓存
        List<Integer> petIds = petMapper.selectIdsByShopIds(ids);
        //查询所有宠物店旗下所有的商品的id，用于后续删除商品相关缓存
        List<Integer> goodsIds = goodsMapper.selectIdsByShopIds(ids);

        //操作数据库
        petMapper.deleteByShopIds(ids);//清空宠物店下的宠物
        goodsMapper.deleteByShopIds(ids);//清空宠物店下的商品
        slideshowMapper.deleteByShopIds(ids);//删除宠物店旗下宠物的轮播图
        petShopMapper.deleteByIds(ids);//删除宠物店

        //清理宠物店相关缓存
        List<String> shopKeys = new ArrayList<>();
        for (Integer id : ids) {
            shopKeys.add(RedisMark.PET_SHOP_DETAIL_PREFIX + id);
        }
        redisUtil.delete(shopKeys);

        //判断是否删除到热门宠物店
        List<PetShopShowVO> listCache = redisUtil.getListByHashValues(RedisMark.PET_SHOP_HOT_KEY, PetShopShowVO.class);
        if (listCache != null) {
            Set<Integer> shopIdsSet = new HashSet<>(ids);
            //删除到热门宠物店，则清理缓存
            for (PetShopShowVO item : listCache) {
                if (shopIdsSet.contains(item.getId())) {
                    redisUtil.delete(RedisMark.PET_SHOP_HOT_KEY);
                    break;
                }
            }
        }

        //清理旗下被删除宠物详情缓存
        List<String> petKeys = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        for (Integer petId : petIds) {
            petKeys.add(RedisMark.PET_DETAIL_PREFIX + petId);
            fields.add(petId.toString());
        }
        redisUtil.delete(petKeys);
        redisUtil.deleteHashValues(RedisMark.PET_RECOMMEND_KEY, fields);


        //判断是否删除到热门商品
        List<GoodsShowVO> goodsListCache = redisUtil.getListByHashValues(RedisMark.GOODS_HOT_KEY, GoodsShowVO.class);
        if (goodsListCache != null) {
            Set<Integer> goodsIdsSet = new HashSet<>(goodsIds);
            //删除到热门商品，则清理缓存
            for (GoodsShowVO item : goodsListCache) {
                if (goodsIdsSet.contains(item.getId())) {
                    redisUtil.delete(RedisMark.GOODS_HOT_KEY);
                    break;
                }
            }
        }
    }

    @Override
    public void edit(PetShop petShop, HttpServletRequest request) {
        //查询手机号是否存在，手机号已存在，则抛出异常
        PetShop petShopDB = petShopMapper.selectByPhone(petShop.getPhone());
        if (petShopDB != null && !StringUtils.equals(petShop.getUsername(), petShopDB.getUsername())) {
            throw new BusinessException(ReturnMsg.PHONE_REGISTER);
        }

        //操作数据库
        petShopMapper.updateById(petShop);

        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.PET_SHOP_DETAIL_PREFIX + petShop.getId());

        //判断是否在修改自身信息，是则更新redis缓存
        if (ThreadUtil.hasPermission(petShop.getId(), RoleType.PETSHOP)) {
            String token = request.getHeader(jwtProperties.getTokenName());
            String profileKey = RedisMark.PROFILE_PREFIX + token;
            redisUtil.updateHashAll(profileKey, petShop);
        }
    }

    @Override
    public PetShop query(Integer id, HttpServletRequest request) {
        //判断是否在查询自身信息，是则查询redis缓存
        if (ThreadUtil.hasPermission(id, RoleType.PETSHOP)) {
            String token = request.getHeader(jwtProperties.getTokenName());
            String profileKey = RedisMark.PROFILE_PREFIX + token;
            PetShop petShopCache = redisUtil.getObjByHash(profileKey, PetShop.class);
            if (petShopCache != null) {
                return petShopCache;
            }
        }

        //查询非自身信息，查询数据库
        PetShop petShop = petShopMapper.selectById(id);
        //不回显密码
        petShop.setPassword(null);
        return petShop;
    }

    @Override
    public PageInfo<PetShop> page(PetShopPageDTO petShopPageDTO) {
        PageHelper.startPage(petShopPageDTO.getPageNum(), petShopPageDTO.getPageSize());
        List<PetShop> list = petShopMapper.selectPage(petShopPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public List<PetShopShowVO> getHot() {
        //查询redis缓存
        String key = RedisMark.PET_SHOP_HOT_KEY;
        List<PetShopShowVO> listCache = redisUtil.getListByHashValues(key, PetShopShowVO.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<PetShopShowVO> listDB = petShopMapper.selectHot6();
        if (CollectionUtils.isNotEmpty(listDB)) {
            //回写redis缓存
            Map<String, String> mapSave = new HashMap<>();
            listDB.forEach(item -> mapSave.put(item.getId().toString(), JSON.toJSONString(item)));
            redisUtil.setHash(key, mapSave, RedisMark.PET_SHOP_HOT_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }

    @Override
    public PageInfo<PetShopShowVO> pagePublic(PetShopPageDTO petShopPageDTO) {
        PageHelper.startPage(petShopPageDTO.getPageNum(), petShopPageDTO.getPageSize());
        List<PetShopShowVO> list = petShopMapper.selectPagePublic(petShopPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public PetShopDetailVO detail(Integer id) {
        //查询redis缓存
        String key = RedisMark.PET_SHOP_DETAIL_PREFIX + id;
        PetShopDetailVO petShopDetailVOCache = redisUtil.getObjByHash(key, PetShopDetailVO.class);

        //命中缓存
        if (petShopDetailVOCache != null) {
            return petShopDetailVOCache;
        }

        //未命中缓存，查询数据库
        PetShopDetailVO petShopDetailVODB = petShopMapper.selectDetail(id);
        if (petShopDetailVODB != null) {
            //写入redis缓存
            redisUtil.setHash(key, petShopDetailVODB, RedisMark.PET_SHOP_DETAIL_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空对象，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return petShopDetailVODB;
    }
}
