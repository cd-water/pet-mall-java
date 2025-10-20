package com.cdwater.petmall;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFileStorage
@EnableScheduling
@MapperScan("com.cdwater.petmall.mapper")
public class PetMallJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetMallJavaApplication.class, args);
    }

}
