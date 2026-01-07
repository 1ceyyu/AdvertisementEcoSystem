package com.example.shopping.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private String category;
    @Column(length = 1000)
    private String description;
    private String imageUrl; // 存储如 "/uploads/abc.jpg"
}