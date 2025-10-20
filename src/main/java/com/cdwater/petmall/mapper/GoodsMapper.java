package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.Goods;
import com.cdwater.petmall.model.dto.GoodsPageDTO;
import com.cdwater.petmall.model.vo.GoodsPageVO;
import com.cdwater.petmall.model.vo.GoodsQueryVO;
import com.cdwater.petmall.model.vo.GoodsShowVO;

import java.util.List;
import java.util.Map;

public interface GoodsMapper {

    List<GoodsPageVO> selectPage(GoodsPageDTO goodsPageDTO);

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> getTypeCountMap();

    Integer countBySaleStatus(Integer onSale);

    void insert(Goods goods);

    void updateById(Goods goods);

    GoodsQueryVO selectById(Integer id);

    List<GoodsShowVO> selectHot12();

    List<GoodsShowVO> selectPagePublic(GoodsPageDTO goodsPageDTO);

    List<Integer> selectIdsByShopId(Integer shopId);

    List<Integer> selectIdsByShopIds(List<Integer> shopIds);

    void subStockAndAddSaleVolume(Integer id, Integer num);

    void addStockAndSubSaleVolume(Integer id, Integer num);

    //删除操作------------------------------------------------------------------------------------------------------------
    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void deleteByShopId(Integer shopId);

    void deleteByShopIds(List<Integer> shopIds);
    //删除操作------------------------------------------------------------------------------------------------------------
}




