package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.Collect;
import com.cdwater.petmall.model.vo.CollectPetVO;

import java.util.List;

public interface CollectService {

    List<CollectPetVO> list();

    void join(Collect collect);

    void out(Integer userId, Integer petId);
}
