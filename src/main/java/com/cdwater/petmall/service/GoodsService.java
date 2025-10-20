package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.Goods;
import com.cdwater.petmall.model.dto.GoodsPageDTO;
import com.cdwater.petmall.model.vo.GoodsPageVO;
import com.cdwater.petmall.model.vo.GoodsQueryVO;
import com.cdwater.petmall.model.vo.GoodsShowVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface GoodsService {

    void add(Goods goods);

    void edit(Goods goods);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    GoodsQueryVO query(Integer id);

    PageInfo<GoodsPageVO> page(GoodsPageDTO goodsPageDTO);

    List<GoodsShowVO> getHot();

    PageInfo<GoodsShowVO> pagePublic(GoodsPageDTO goodsPageDTO);
}
