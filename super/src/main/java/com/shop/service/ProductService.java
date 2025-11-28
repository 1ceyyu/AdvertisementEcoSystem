package com.shop.service;

import com.shop.entity.Product;

import java.util.List;

public interface ProductService {
    // 获取所有商品
    List<Product> findAllProducts();
    
    // 根据分类ID获取商品
    List<Product> findProductsByCategoryId(int categoryId);
    
    // 根据ID查找商品
    Product findProductById(int id);
    
    // 添加商品
    void addProduct(Product product);
    
    // 更新商品
    void updateProduct(Product product);
    
    // 删除商品
    void deleteProduct(int id);
}