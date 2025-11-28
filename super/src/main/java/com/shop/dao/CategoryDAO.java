package com.shop.dao;

import com.shop.entity.Category;

import java.util.List;

public interface CategoryDAO {
    // 获取所有分类
    List<Category> findAll();
    
    // 根据ID查找分类
    Category findById(int id);
    
    // 添加分类
    void add(Category category);
    
    // 更新分类
    void update(Category category);
    
    // 删除分类
    void delete(int id);
}