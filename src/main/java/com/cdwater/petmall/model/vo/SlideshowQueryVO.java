package com.cdwater.petmall.model.vo;

import lombok.Data;

/**
 * 后台轮播图查询VO
 */
@Data
public class SlideshowQueryVO {
    private Integer id;
    private Integer petId;
    private Integer shopId;
    private String img;
}
