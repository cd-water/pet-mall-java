package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.PetType;

import java.util.List;

public interface PetTypeMapper {

    void insert(PetType petType);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void updateById(PetType petType);

    PetType selectById(Integer id);

    List<PetType> selectPage(PetType petType);

    PetType selectByName(String name);

    List<PetType> selectAll();

    List<PetType> selectHot4();
}




