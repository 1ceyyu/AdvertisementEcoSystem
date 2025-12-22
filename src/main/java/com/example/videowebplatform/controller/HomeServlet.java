// File: com/example/videowebplatform/controller/HomeServlet.java
package com.example.videowebplatform.controller;

import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.model.Video;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    // 面向接口编程
    private final VideoDAO videoDAO = new VideoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取视频列表
        List<Video> videoList = videoDAO.getAllVideos();
        // 将数据设置到请求属性中
        request.setAttribute("videoList", videoList);
        //转发到首页 JSP
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}