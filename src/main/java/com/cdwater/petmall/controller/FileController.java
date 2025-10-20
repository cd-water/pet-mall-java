package com.cdwater.petmall.controller;

import com.cdwater.petmall.common.Result;
import jakarta.annotation.Resource;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class FileController {

    @Resource
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        //指定oss存储路径
        String relativePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";

        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(relativePath)
                .upload();
        return Result.success(fileInfo == null ? "上传失败！" : fileInfo.getUrl());
    }
}
