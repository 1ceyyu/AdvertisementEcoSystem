<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.media.news.model.NewsArticle" %>
<% NewsArticle news = (NewsArticle)request.getAttribute("news"); %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= news.getTitle() %> - 每日新闻</title>

    <!-- 核心点3：在Meta标签中声明内容分类 -->
    <meta name="content-category" content="<%= news.getCategory() %>">

    <!-- 引入 Bootstrap 5 实现大气美观的UI -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; font-family: 'Georgia', serif; }
        .news-header { border-bottom: 1px solid #ddd; margin-bottom: 20px; padding-bottom: 10px; }
        .news-meta { color: #6c757d; font-size: 0.9rem; margin-bottom: 20px; }
        .content-body { font-size: 1.1rem; line-height: 1.8; color: #333; }

        /* 核心点2：广告位样式 */
        .ad-sidebar {
            background-color: #fff;
            border: 1px solid #e9ecef;
            padding: 15px;
            border-radius: 5px;
            text-align: center;
            min-height: 300px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.05);
        }
        .ad-placeholder {
            background-color: #eee;
            color: #888;
            height: 250px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 2px dashed #ccc;
        }
    </style>
</head>
<body>

<!-- 导航栏 -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="#">Daily News Media</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="article?id=1">科技</a></li>
                <li class="nav-item"><a class="nav-link" href="article?id=2">体育</a></li>
                <li class="nav-item"><a class="nav-link" href="article?id=3">财经</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row">
        <!-- 左侧：新闻内容区域 -->
        <div class="col-lg-8">
            <article class="bg-white p-5 shadow-sm rounded">
                <div class="news-header">
                    <span class="badge bg-primary mb-2"><%= news.getCategory().toUpperCase() %></span>
                    <h1 class="display-6 fw-bold"><%= news.getTitle() %></h1>
                    <div class="news-meta">
                        <span>作者: <%= news.getAuthor() %></span> |
                        <span>日期: <%= news.getDate() %></span>
                    </div>
                </div>

                <div class="content-body">
                    <p><%= news.getContent() %></p>
                    <p>（此处模拟更多新闻正文内容...用户正在阅读<%= news.getCategory() %>类文章，系统正在后台记录行为。）</p>
                </div>
            </article>
        </div>

        <!-- 右侧：广告侧边栏 -->
        <div class="col-lg-4">
            <div class="ad-sidebar sticky-top" style="top: 20px;">
                <h5 class="text-muted mb-3">赞助商推荐</h5>

                <!-- 核心点2：专门留出的广告空间 -->
                <div id="ad-container" class="ad-placeholder">
                    <div class="spinner-border text-secondary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <p class="mt-2">正在基于您的兴趣加载广告...</p>
                </div>

            </div>
        </div>
    </div>
</div>

<!-- 脚注 -->
<footer class="text-center py-4 text-muted mt-5">
    <small>&copy; 2025 Daily News Media. Part of AdTech Demo Network.</small>
</footer>

<!-- 核心点4 & 5：引入追踪与广告加载脚本 -->
<script src="js/ad-tracker.js"></script>

</body>
</html>
