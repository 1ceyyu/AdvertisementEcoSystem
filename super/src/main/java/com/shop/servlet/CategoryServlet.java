package com.shop.servlet;

import com.shop.entity.Category;
import com.shop.service.CategoryService;
import com.shop.service.CategoryServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryServlet extends HttpServlet {
    private CategoryService categoryService = new CategoryServiceImpl();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listCategories(request, response);
                break;
            case "detail":
                getCategoryDetail(request, response);
                break;
            case "delete":
                deleteCategory(request, response);
                break;
            default:
                listCategories(request, response);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "add":
                addCategory(request, response);
                break;
            case "update":
                updateCategory(request, response);
                break;
            default:
                listCategories(request, response);
        }
    }
    
    // 获取所有分类
    private void listCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categories = categoryService.findAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/admin/category/list.jsp").forward(request, response);
    }
    
    // 获取分类详情
    private void getCategoryDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Category category = categoryService.findCategoryById(id);
        request.setAttribute("category", category);
        request.getRequestDispatcher("/admin/category/detail.jsp").forward(request, response);
    }
    
    // 添加分类
    private void addCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        
        Category category = new Category(name, description);
        categoryService.addCategory(category);
        
        response.sendRedirect(request.getContextPath() + "/category?action=list");
    }
    
    // 更新分类
    private void updateCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        
        Category category = new Category(name, description);
        category.setId(id);
        categoryService.updateCategory(category);
        
        response.sendRedirect(request.getContextPath() + "/category?action=list");
    }
    
    // 删除分类
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        categoryService.deleteCategory(id);
        response.sendRedirect(request.getContextPath() + "/category?action=list");
    }
}