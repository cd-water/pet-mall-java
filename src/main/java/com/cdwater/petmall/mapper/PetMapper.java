package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.Pet;
import com.cdwater.petmall.model.dto.PetPageDTO;
import com.cdwater.petmall.model.vo.*;

import java.util.List;
import java.util.Map;

public interface PetMapper {

    void updateById(Pet pet);

    List<PetPageVO> selectPage(PetPageDTO petPageDTO);

    List<PetGroupVO> selectPetGroup();

    Integer countBySaleStatus(Integer saleStatus);

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> getTypeCountMap();

    void insert(Pet pet);

    PetQueryVO selectById(Integer id);

    List<PetShowVO> selectRecommend12();

    List<PetShowVO> selectPagePublic(PetPageDTO petPageDTO);

    PetDetailVO selectDetail(Integer id);

    List<Integer> selectIdsByShopId(Integer shopId);

    List<Integer> selectIdsByShopIds(List<Integer> shopIds);

    void subStock(Integer id);

    void addStock(Integer id);

    //删除操作------------------------------------------------------------------------------------------------------------
    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void deleteByShopId(Integer shopId);

    void deleteByShopIds(List<Integer> shopIds);
    //删除操作------------------------------------------------------------------------------------------------------------
}




