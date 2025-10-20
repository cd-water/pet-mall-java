package com.cdwater.petmall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.mapper.SlideshowMapper;
import com.cdwater.petmall.model.dto.SlideshowPageDTO;
import com.cdwater.petmall.entity.Slideshow;
import com.cdwater.petmall.model.vo.SlideshowPageVO;
import com.cdwater.petmall.model.vo.SlideshowQueryVO;
import com.cdwater.petmall.service.SlideshowService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SlideshowServiceImpl implements SlideshowService {

    @Resource
    private SlideshowMapper slideshowMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void add(Slideshow slideshow) {
        //操作数据库
        slideshowMapper.insert(slideshow);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.SLIDESHOW_KEY);
    }

    @Override
    public void removeOne(Integer id) {
        //操作数据库
        slideshowMapper.deleteById(id);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.SLIDESHOW_KEY);
    }

    @Override
    public void removeBatch(List<Integer> ids) {
        //操作数据库
        slideshowMapper.deleteByIds(ids);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.SLIDESHOW_KEY);
    }

    @Override
    public void edit(Slideshow slideshow) {
        //操作数据库
        slideshowMapper.updateById(slideshow);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.SLIDESHOW_KEY);
    }

    @Override
    public SlideshowQueryVO query(Integer id) {
        return slideshowMapper.selectById(id);
    }

    @Override
    public PageInfo<SlideshowPageVO> page(SlideshowPageDTO slideshowPageDTO) {
        PageHelper.startPage(slideshowPageDTO.getPageNum(), slideshowPageDTO.getPageSize());
        List<SlideshowPageVO> list = slideshowMapper.selectPage(slideshowPageDTO);
        return PageInfo.of(list);
    }

    @Override
    public List<Slideshow> list() {
        //查询redis缓存
        String key = RedisMark.SLIDESHOW_KEY;
        List<Slideshow> listCache = redisUtil.getListByHashValues(key, Slideshow.class);

        //命中缓存
        if (listCache != null) {
            return listCache;
        }

        //未命中缓存，查询数据库
        List<Slideshow> listDB = slideshowMapper.selectList();
        if (CollectionUtils.isNotEmpty(listDB)) {
            Map<String, String> mapSave = new HashMap<>();
            listDB.forEach(item -> mapSave.put(item.getPetId().toString(), JSON.toJSONString(item)));
            //回写redis缓存
            redisUtil.setHash(key, mapSave, RedisMark.SLIDESHOW_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setHash(key, RedisMark.EMPTY_MAP, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return listDB;
    }
}
