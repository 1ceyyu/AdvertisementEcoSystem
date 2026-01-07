package com.example.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer; // 引入这个
import org.springframework.boot.builder.SpringApplicationBuilder; // 引入这个

@SpringBootApplication
public class ShoppingApplication extends SpringBootServletInitializer { // 1. 继承这个类

    // 2. 重写 configure 方法
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShoppingApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplication.class, args);
    }
}