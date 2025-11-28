<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>商品详情 - 购物网站</title>
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
        .product-detail {
            display: flex;
            gap: 20px;
            margin-top: 20px;
        }
        .product-info {
            flex: 1;
        }
        .price {
            color: #f44336;
            font-weight: bold;
            font-size: 24px;
            margin: 10px 0;
        }
        .back-btn {
            display: inline-block;
            padding: 8px 16px;
            background-color: #f1f1f1;
            color: #333;
            text-decoration: none;
            border-radius: 3px;
            margin-bottom: 20px;
        }
        .back-btn:hover {
            background-color: #ddd;
        }
        .btn {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 3px;
            display: inline-block;
            margin-top: 20px;
        }
        .btn:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>购物网站</h1>
        <div>
            <span>欢迎，${sessionScope.user.username}</span>
            <a href="${pageContext.request.contextPath}/logout" style="color: white; margin-left: 20px;">退出登录</a>
        </div>
    </div>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/customer/index.jsp">首页</a>
        <a href="${pageContext.request.contextPath}/record?action=listByUser">浏览记录</a>
    </div>
    
    <div class="content">
        <div class="card">
            <a href="${pageContext.request.contextPath}/product?action=listByCategory&categoryId=${product.categoryId}" class="back-btn">返回商品列表</a>
            
            <h2>商品详情</h2>
            
            <div class="product-detail">
                <div class="product-info">
                    <h3>${product.name}</h3>
                    <div class="price">¥${product.price}</div>
                    <p><strong>描述:</strong> ${product.description}</p>
                    <a href="#" class="btn">加入购物车</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>