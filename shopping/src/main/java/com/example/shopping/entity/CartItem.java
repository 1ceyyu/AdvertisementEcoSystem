package com.example.shopping.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @ManyToOne
    private Product product;
    private Integer quantity;
}