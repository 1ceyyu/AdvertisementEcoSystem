package com.shop.servlet;

import com.shop.entity.Product;
import com.shop.service.CategoryService;
import com.shop.service.CategoryServiceImpl;
import com.shop.service.ProductService;
import com.shop.service.ProductServiceImpl;
import com.shop.service.ViewRecordService;
import com.shop.service.ViewRecordServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ProductServlet extends HttpServlet {
    private ProductService productService = new ProductServiceImpl();
    private CategoryService categoryService = new CategoryServiceImpl();
    private ViewRecordService viewRecordService = new ViewRecordServiceImpl();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listProducts(request, response);
                break;
            case "listByCategory":
                listProductsByCategory(request, response);
                break;
            case "detail":
                getProductDetail(request, response);
                break;
            case "delete":
                deleteProduct(request, response);
                break;
            default:
                listProducts(request, response);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "add":
                addProduct(request, response);
                break;
            case "update":
                updateProduct(request, response);
                break;
            default:
                listProducts(request, response);
        }
    }
    
    // 获取所有商品
    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Product> products = productService.findAllProducts();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);
    }
    
    // 根据分类获取商品
    private void listProductsByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        List<Product> products = productService.findProductsByCategoryId(categoryId);
        
        // 记录用户浏览该分类
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");
        if (userObj != null && !"admin".equals(((com.shop.entity.User) userObj).getRole())) {
            int userId = ((com.shop.entity.User) userObj).getId();
            viewRecordService.addOrUpdateViewRecord(userId, categoryId);
        }
        
        request.setAttribute("products", products);
        request.setAttribute("categoryId", categoryId);
        request.getRequestDispatcher("/customer/product/list.jsp").forward(request, response);
    }
    
    // 获取商品详情
    private void getProductDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productService.findProductById(id);
        request.setAttribute("product", product);
        request.getRequestDispatcher("/customer/product/detail.jsp").forward(request, response);
    }
    
    // 添加商品
    private void addProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        
        Product product = new Product(name, description, price, categoryId);
        productService.addProduct(product);
        
        response.sendRedirect(request.getContextPath() + "/product?action=list");
    }
    
    // 更新商品
    private void updateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        
        Product product = new Product(name, description, price, categoryId);
        product.setId(id);
        productService.updateProduct(product);
        
        response.sendRedirect(request.getContextPath() + "/product?action=list");
    }
    
    // 删除商品
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        productService.deleteProduct(id);
        response.sendRedirect(request.getContextPath() + "/product?action=list");
    }
}