<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>分类管理 - 购物网站</title>
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
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
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
        }
        .btn-secondary {
            background-color: #2196F3;
            color: white;
        }
        .btn-danger {
            background-color: #f44336;
            color: white;
        }
        .btn-primary:hover {
            background-color: #45a049;
        }
        .btn-secondary:hover {
            background-color: #0b7dda;
        }
        .btn-danger:hover {
            background-color: #d32f2f;
        }
        .add-btn {
            margin-bottom: 20px;
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
            <h2>分类管理</h2>
            
            <a href="${pageContext.request.contextPath}/admin/category/add.jsp" class="btn btn-primary add-btn">添加分类</a>
            
            <table>
                <tr>
                    <th>ID</th>
                    <th>名称</th>
                    <th>描述</th>
                    <th>操作</th>
                </tr>
                <c:forEach items="${categories}" var="category">
                    <tr>
                        <td>${category.id}</td>
                        <td>${category.name}</td>
                        <td>${category.description}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/category/edit.jsp?id=${category.id}" class="btn btn-secondary">编辑</a>
                            <a href="${pageContext.request.contextPath}/category?action=delete&id=${category.id}" class="btn btn-danger" onclick="return confirm('确定要删除该分类吗？')">删除</a>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</body>
</html>