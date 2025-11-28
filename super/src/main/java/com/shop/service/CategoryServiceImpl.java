package com.shop.service;

import com.shop.dao.CategoryDAO;
import com.shop.dao.CategoryDAOImpl;
import com.shop.entity.Category;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDAO categoryDAO = new CategoryDAOImpl();
    
    @Override
    public List<Category> findAllCategories() {
        return categoryDAO.findAll();
    }
    
    @Override
    public Category findCategoryById(int id) {
        return categoryDAO.findById(id);
    }
    
    @Override
    public void addCategory(Category category) {
        categoryDAO.add(category);
    }
    
    @Override
    public void updateCategory(Category category) {
        categoryDAO.update(category);
    }
    
    @Override
    public void deleteCategory(int id) {
        categoryDAO.delete(id);
    }
}