package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.GoodsType;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface GoodsTypeService {
    void add(GoodsType goodsType);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(GoodsType goodsType);

    GoodsType query(Integer id);

    PageInfo<GoodsType> page(GoodsType goodsType, Integer pageNum, Integer pageSize);

    List<GoodsType> getAll();

    List<GoodsType> getHot();
}
