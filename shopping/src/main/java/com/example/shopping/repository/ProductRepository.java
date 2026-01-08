package com.example.shopping.repository;
import com.example.shopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 基础功能已足够，复杂筛选用Java Stream实现
}