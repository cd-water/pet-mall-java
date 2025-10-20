package com.cdwater.petmall.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class GoodsOrdersPageDTO {

    private Long orderNo;

    private Integer orderStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    private Integer shopId;

    private Integer pageNum;

    private Integer pageSize;
}