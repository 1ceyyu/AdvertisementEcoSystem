package com.example.shopping.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略API中我们不需要的字段
public class AdDto {
    private Long id;
    private String title;
    private String type; // image 或 video

    @JsonProperty("media_url") // 映射 JSON 中的 media_url
    private String mediaUrl;

    @JsonProperty("target_url")
    private String targetUrl;

    private String category;
}