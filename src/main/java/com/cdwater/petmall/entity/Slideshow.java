package com.cdwater.petmall.entity;

import lombok.Data;

/**
 * 轮播图实体类
 */
@Data
public class Slideshow {

    private Integer id;

    private Integer petId;

    private Integer shopId;

    private String img;
}