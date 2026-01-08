package com.news.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class UserBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 前端生成的 UUID (匿名用户标识)
    private String userUuid;

    // 点击的板块 (education, games...)
    private String category;

    private Date clickTime = new Date();
}