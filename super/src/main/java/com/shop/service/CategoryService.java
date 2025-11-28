package com.shop.service;

import com.shop.entity.Category;

import java.util.List;

public interface CategoryService {
    // 获取所有分类
    List<Category> findAllCategories();
    
    // 根据ID查找分类
    Category findCategoryById(int id);
    
    // 添加分类
    void addCategory(Category category);
    
    // 更新分类
    void updateCategory(Category category);
    
    // 删除分类
    void deleteCategory(int id);
}