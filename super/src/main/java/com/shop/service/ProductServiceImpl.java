package com.shop.service;

import com.shop.dao.ProductDAO;
import com.shop.dao.ProductDAOImpl;
import com.shop.entity.Product;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDAO productDAO = new ProductDAOImpl();
    
    @Override
    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }
    
    @Override
    public List<Product> findProductsByCategoryId(int categoryId) {
        return productDAO.findByCategoryId(categoryId);
    }
    
    @Override
    public Product findProductById(int id) {
        return productDAO.findById(id);
    }
    
    @Override
    public void addProduct(Product product) {
        productDAO.add(product);
    }
    
    @Override
    public void updateProduct(Product product) {
        productDAO.update(product);
    }
    
    @Override
    public void deleteProduct(int id) {
        productDAO.delete(id);
    }
}