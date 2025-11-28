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
        .record-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px;
            background-color: #f9f9f9;
            border-radius: 5px;
            margin-bottom: 10px;
        }
        .record-info h3 {
            margin: 0;
            color: #2196F3;
        }
        .record-stats {
            text-align: right;
        }
        .view-count {
            font-weight: bold;
            color: #4CAF50;
        }
        .last-view {
            color: #666;
            font-size: 14px;
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
            <h2>我的浏览记录</h2>
            
            <c:if test="${empty records}">
                <p>您还没有浏览记录。</p>
            </c:if>
            
            <c:if test="${not empty records}">
                <div class="records-list">
                    <c:forEach items="${records}" var="record">
                        <div class="record-item">
                            <div class="record-info">
                                <h3>分类 ID: ${record.categoryId}</h3>
                            </div>
                            <div class="record-stats">
                                <div class="view-count">浏览次数: ${record.viewCount}</div>
                                <div class="last-view">最后浏览: ${record.lastViewedAt}</div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>