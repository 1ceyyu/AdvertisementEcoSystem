package com.news.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
// 联合唯一索引：保证同一个用户对同一个分类只有一条记录
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userUuid", "category"})})
public class UserInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userUuid;

    private String category;

    private Integer score = 0; // 兴趣分，默认为0
}