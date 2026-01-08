package com.news.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String content; // 新闻简介或内容

    // 分类: education, entertainment, games, health, tech
    private String category;

    private Date createTime = new Date();
}