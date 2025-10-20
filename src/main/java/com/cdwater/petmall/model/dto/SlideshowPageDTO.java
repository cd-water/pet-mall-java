package com.cdwater.petmall.model.dto;

import lombok.Data;

/**
 * 轮播图分页DTO
 */
@Data
public class SlideshowPageDTO {
    //分页参数
    private Integer pageNum;
    private Integer pageSize;

    //搜索条件
    private String petName;
    private String shopName;
}
