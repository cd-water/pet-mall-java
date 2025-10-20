package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.Notice;

import java.util.List;

public interface NoticeMapper {

    void insert(Notice notice);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void updateById(Notice notice);

    Notice selectById(Integer id);

    List<Notice> selectPage(Notice notice);

    Notice selectLatest();
}




