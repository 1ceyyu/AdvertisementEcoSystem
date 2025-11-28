package com.shop.servlet;

import com.shop.entity.ViewRecord;
import com.shop.service.ViewRecordService;
import com.shop.service.ViewRecordServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ViewRecordServlet extends HttpServlet {
    private ViewRecordService viewRecordService = new ViewRecordServiceImpl();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listViewRecords(request, response);
                break;
            case "listByUser":
                listViewRecordsByUser(request, response);
                break;
            default:
                listViewRecords(request, response);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    
    // 获取所有浏览记录（管理员使用）
    private void listViewRecords(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ViewRecord> records = viewRecordService.findAllViewRecords();
        request.setAttribute("records", records);
        request.getRequestDispatcher("/admin/record/list.jsp").forward(request, response);
    }
    
    // 获取当前用户的浏览记录（消费者使用）
    private void listViewRecordsByUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");
        
        if (userObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        int userId = ((com.shop.entity.User) userObj).getId();
        List<ViewRecord> records = viewRecordService.findViewRecordsByUserId(userId);
        
        request.setAttribute("records", records);
        request.getRequestDispatcher("/customer/record/list.jsp").forward(request, response);
    }
}