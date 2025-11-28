package com.shop.dao;

import com.shop.entity.Product;

import java.util.List;

public interface ProductDAO {
    // 获取所有商品
    List<Product> findAll();
    
    // 根据分类ID获取商品
    List<Product> findByCategoryId(int categoryId);
    
    // 根据ID查找商品
    Product findById(int id);
    
    // 添加商品
    void add(Product product);
    
    // 更新商品
    void update(Product product);
    
    // 删除商品
    void delete(int id);
}