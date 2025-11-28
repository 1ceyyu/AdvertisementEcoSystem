<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>编辑分类 - 购物网站</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f0f0f0;
        }
        .header {
            background-color: #333;
            color: white;
            padding: 10px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h1 {
            margin: 0;
            font-size: 24px;
        }
        .nav {
            background-color: #4CAF50;
            padding: 10px 20px;
        }
        .nav a {
            color: white;
            text-decoration: none;
            margin-right: 20px;
            font-weight: bold;
        }
        .content {
            padding: 20px;
        }
        .card {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        h2 {
            color: #333;
            border-bottom: 1px solid #ddd;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], textarea {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        textarea {
            height: 100px;
            resize: vertical;
        }
        .btn {
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 3px;
            cursor: pointer;
            margin-right: 5px;
            display: inline-block;
        }
        .btn-primary {
            background-color: #4CAF50;
            color: white;
            border: none;
        }
        .btn-secondary {
            background-color: #f1f1f1;
            color: #333;
        }
        .btn-primary:hover {
            background-color: #45a049;
        }
        .btn-secondary:hover {
            background-color: #ddd;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>管理员中心</h1>
        <div>
            <span>欢迎，${sessionScope.user.username}</span>
            <a href="${pageContext.request.contextPath}/logout" style="color: white; margin-left: 20px;">退出登录</a>
        </div>
    </div>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/admin/index.jsp">首页</a>
        <a href="${pageContext.request.contextPath}/category?action=list">分类管理</a>
        <a href="${pageContext.request.contextPath}/product?action=list">商品管理</a>
        <a href="${pageContext.request.contextPath}/record?action=list">浏览记录</a>
    </div>
    
    <div class="content">
        <div class="card">
            <h2>编辑分类</h2>
            
            <form action="${pageContext.request.contextPath}/category?action=update" method="post">
                <input type="hidden" name="id" value="${category.id}">
                <div class="form-group">
                    <label for="name">分类名称:</label>
                    <input type="text" id="name" name="name" value="${category.name}" required>
                </div>
                <div class="form-group">
                    <label for="description">分类描述:</label>
                    <textarea id="description" name="description">${category.description}</textarea>
                </div>
                <div class="form-group">
                    <input type="submit" value="更新" class="btn btn-primary">
                    <a href="${pageContext.request.contextPath}/category?action=list" class="btn btn-secondary">返回列表</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>