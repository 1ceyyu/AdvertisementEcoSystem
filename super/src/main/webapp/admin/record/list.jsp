<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>浏览记录 - 购物网站</title>
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
            <h2>浏览记录</h2>
            
            <table>
                <tr>
                    <th>ID</th>
                    <th>用户ID</th>
                    <th>分类ID</th>
                    <th>浏览次数</th>
                    <th>最后浏览时间</th>
                </tr>
                <c:forEach items="${records}" var="record">
                    <tr>
                        <td>${record.id}</td>
                        <td>${record.userId}</td>
                        <td>${record.categoryId}</td>
                        <td>${record.viewCount}</td>
                        <td>${record.lastViewTime}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</body>
</html>