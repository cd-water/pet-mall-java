package com.cdwater.petmall.mapper;

import com.cdwater.petmall.model.dto.SlideshowPageDTO;
import com.cdwater.petmall.entity.Slideshow;
import com.cdwater.petmall.model.vo.SlideshowPageVO;
import com.cdwater.petmall.model.vo.SlideshowQueryVO;

import java.util.List;

public interface SlideshowMapper {

    void insert(Slideshow slideshow);

    void updateById(Slideshow slideshow);

    SlideshowQueryVO selectById(Integer id);

    List<SlideshowPageVO> selectPage(SlideshowPageDTO slideshowPageDTO);

    List<Slideshow> selectList();

    //删除操作------------------------------------------------------------------------------------------------------------
    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void deleteByPetId(Integer petId);

    void deleteByPetIds(List<Integer> petIds);

    void deleteByShopId(Integer shopId);

    void deleteByShopIds(List<Integer> shopIds);
    //删除操作------------------------------------------------------------------------------------------------------------
}




