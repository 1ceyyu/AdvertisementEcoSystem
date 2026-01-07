package com.example.shopping.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 注意：路径结尾必须加斜杠 /
    // Windows 路径中的反斜杠 \ 建议在 Java 字符串中写成 /
    public static final String LOCAL_PATH = "C:/Users/27410/Desktop/计算机/计算机代码及文件/web/po/";
    public static final String SERVER_PATH = "/home/shopping_images/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name");

        // 映射逻辑：浏览器访问 /uploads/xxx.jpg -> 读取本地磁盘对应文件
        if (os.toLowerCase().startsWith("win")) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + LOCAL_PATH);
        } else {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + SERVER_PATH);
        }
    }
}