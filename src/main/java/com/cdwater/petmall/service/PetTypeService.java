package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.PetType;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface PetTypeService {
    void add(PetType petType);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(PetType petType);

    PetType query(Integer id);

    PageInfo<PetType> page(PetType petType, Integer pageNum, Integer pageSize);

    List<PetType> getAll();

    List<PetType> getHot();
}
