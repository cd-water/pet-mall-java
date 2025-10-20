package com.cdwater.petmall.mapper;

import com.cdwater.petmall.entity.PetShop;
import com.cdwater.petmall.model.dto.PetShopPageDTO;
import com.cdwater.petmall.model.vo.PetShopShowVO;
import com.cdwater.petmall.model.vo.PetShopDetailVO;

import java.util.List;

public interface PetShopMapper {

    void insert(PetShop petShop);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> ids);

    void updateById(PetShop petShop);

    PetShop selectById(Integer id);

    List<PetShop> selectPage(PetShopPageDTO petShopPageDTO);

    PetShop selectByUsername(String username);

    PetShop selectByPhone(String phone);

    Integer countByAuditStatus(Integer auditStatus);

    List<PetShopShowVO> selectHot6();

    List<PetShopShowVO> selectPagePublic(PetShopPageDTO petShopPageDTO);

    PetShopDetailVO selectDetail(Integer id);
}




