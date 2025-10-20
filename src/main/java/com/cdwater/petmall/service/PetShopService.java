package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.PetShop;
import com.cdwater.petmall.model.dto.PetShopPageDTO;
import com.cdwater.petmall.model.vo.PetShopShowVO;
import com.cdwater.petmall.model.vo.PetShopDetailVO;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PetShopService {
    void add(PetShop petShop);

    void removeOne(Integer id);

    void removeBatch(List<Integer> ids);

    void edit(PetShop petShop, HttpServletRequest request);

    PetShop query(Integer id, HttpServletRequest request);

    PageInfo<PetShop> page(PetShopPageDTO petShopPageDTO);

    List<PetShopShowVO> getHot();

    PageInfo<PetShopShowVO> pagePublic(PetShopPageDTO petShopPageDTO);

    PetShopDetailVO detail(Integer id);
}
