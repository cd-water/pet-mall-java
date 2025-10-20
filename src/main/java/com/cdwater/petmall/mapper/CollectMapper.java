package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.Collect;
import com.cdwater.petmall.model.vo.CollectPetVO;

import java.util.List;

public interface CollectMapper {

    void insert(Collect collect);

    Collect selectByUserIdAndPetId(Integer userId, Integer petId);

    List<CollectPetVO> selectByUserId(Integer userId);

    //删除操作------------------------------------------------------------------------------------------------------------
    void deleteByUserIdAndPetId(Integer userId, Integer petId);

    void deleteByUserId(Integer userId);

    void deleteByUserIds(List<Integer> userIds);

    void deleteByPetId(Integer petId);

    void deleteByPetIds(List<Integer> petIds);
    //删除操作------------------------------------------------------------------------------------------------------------
}




