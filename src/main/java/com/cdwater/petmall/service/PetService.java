package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.Pet;
import com.cdwater.petmall.model.dto.PetPageDTO;
import com.cdwater.petmall.model.vo.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface PetService {

    void add(Pet pet);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(Pet pet);

    PetQueryVO query(Integer id);

    PageInfo<PetPageVO> page(PetPageDTO petPageDTO);

    List<PetGroupVO> petGroup();

    List<PetShowVO> getRecommend();

    PetDetailVO detail(Integer id, Integer userId, Integer role);

    PageInfo<PetShowVO> pagePublic(PetPageDTO petPageDTO);
}
