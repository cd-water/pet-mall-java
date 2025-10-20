package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.GoodsType;

import java.util.List;

public interface GoodsTypeMapper {

    void insert(GoodsType goodsType);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void updateById(GoodsType goodsType);

    GoodsType selectById(Integer id);

    List<GoodsType> selectPage(GoodsType goodsType);

    GoodsType selectByName(String name);

    List<GoodsType> selectAll();

    List<GoodsType> selectHot4();
}




