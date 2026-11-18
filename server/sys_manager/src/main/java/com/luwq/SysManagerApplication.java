package com.luwq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.luwq.mapper")
public class SysManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SysManagerApplication.class, args);
    }
}
