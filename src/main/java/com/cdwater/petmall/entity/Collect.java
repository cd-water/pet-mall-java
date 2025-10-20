package com.cdwater.petmall.entity;

import lombok.Data;

/**
 * 收藏实体类
 */
@Data
public class Collect {

    private Integer id;

    private Integer userId;

    private Integer petId;
}