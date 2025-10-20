package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.Notice;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface NoticeService {
    void add(Notice notice);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(Notice notice);

    Notice query(Integer id);

    PageInfo<Notice> page(Notice notice, Integer pageNum, Integer pageSize);

    Notice latest();
}
