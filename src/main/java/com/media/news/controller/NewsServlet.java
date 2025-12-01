package com.media.news.controller;

import com.media.news.model.NewsArticle;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/article")
public class NewsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        NewsArticle article;

        // 模拟数据库获取不同分类的新闻
        if ("1".equals(id)) {
            article = new NewsArticle(
                    "2025年新款智能手机发布，AI功能成亮点",
                    "在最新的发布会上，科技巨头展示了集成了深度学习芯片的手机... (此处省略一万字)",
                    "technology", // 分类：科技
                    "TechEditor",
                    "2025-11-29"
            );
        } else if ("2".equals(id)) {
            article = new NewsArticle(
                    "本季篮球决赛前瞻：红队能否卫冕？",
                    "经过漫长的赛季，决赛即将在本周末拉开帷幕... (此处省略一万字)",
                    "sports", // 分类：体育
                    "SportFan",
                    "2025-11-29"
            );
        } else {
            // 默认新闻
            article = new NewsArticle(
                    "全球股市动荡，投资者何去何从",
                    "华尔街今日开盘大跌，受宏观经济影响... (此处省略一万字)",
                    "finance", // 分类：财经
                    "MoneyMaker",
                    "2025-11-29"
            );
        }

        request.setAttribute("news", article);
        request.getRequestDispatcher("/article.jsp").forward(request, response);
    }
}