package com.cdwater.petmall.service;

import com.cdwater.petmall.model.dto.SlideshowPageDTO;
import com.cdwater.petmall.entity.Slideshow;
import com.cdwater.petmall.model.vo.SlideshowPageVO;
import com.cdwater.petmall.model.vo.SlideshowQueryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface SlideshowService {

    void add(Slideshow slideshow);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(Slideshow slideshow);

    SlideshowQueryVO query(Integer id);

    PageInfo<SlideshowPageVO> page(SlideshowPageDTO slideshowPageDTO);

    List<Slideshow> list();
}
