package com.clone.ohouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.clone.ohouse.mapper")
public class OhouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(OhouseApplication.class, args);
    }
    
}
