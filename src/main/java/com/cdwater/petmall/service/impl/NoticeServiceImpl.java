package com.cdwater.petmall.service.impl;

import com.cdwater.petmall.common.constants.RedisMark;
import com.cdwater.petmall.common.utils.RedisUtil;
import com.cdwater.petmall.mapper.NoticeMapper;
import com.cdwater.petmall.entity.Notice;
import com.cdwater.petmall.service.NoticeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void add(Notice notice) {
        //操作数据库
        noticeMapper.insert(notice);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.NOTICE_KEY);
    }

    @Override
    public void removeOne(Integer id) {
        //操作数据库
        noticeMapper.deleteById(id);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.NOTICE_KEY);
    }

    @Override
    public void removeBatch(List<Integer> ids) {
        //操作数据库
        noticeMapper.deleteByIds(ids);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.NOTICE_KEY);
    }

    @Override
    public void edit(Notice notice) {
        //操作数据库
        noticeMapper.updateById(notice);
        //清理缓存，保证数据一致性
        redisUtil.delete(RedisMark.NOTICE_KEY);
    }

    @Override
    public Notice query(Integer id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public PageInfo<Notice> page(Notice notice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> list = noticeMapper.selectPage(notice);
        return PageInfo.of(list);
    }

    @Override
    public Notice latest() {
        //查询redis缓存
        String key = RedisMark.NOTICE_KEY;
        Notice noticeCache = redisUtil.getObjByJson(key, Notice.class);

        //命中缓存
        if (noticeCache != null) {
            return noticeCache;
        }

        //未命中缓存，查询数据库
        Notice noticeDB = noticeMapper.selectLatest();
        if (noticeDB != null) {
            //回写redis缓存
            redisUtil.setJson(key, noticeDB, RedisMark.NOTICE_TTL, TimeUnit.SECONDS, false);
        } else {
            //数据库无数据，缓存空标记，防止缓存穿透
            redisUtil.setStr(key, RedisMark.EMPTY, RedisMark.EMPTY_TTL, TimeUnit.SECONDS, false);
        }
        return noticeDB;
    }
}
