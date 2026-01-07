package com.example.shopping.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class UserInterest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String category;
    private Integer clickCount;
}