const express = require('express');
const cors = require('cors');
require('dotenv').config();
const db = require('./config/db'); // 引入我们配置好的数据库连接池

const app = express();
const PORT = process.env.PORT || 3001;

// ===========================
// 1. 中间件配置 (Middleware)
// ===========================

// 允许跨域请求 (这样前端 5173 才能访问 后端 3001)
app.use(cors());

// 解析 JSON 格式的请求体 (也就是前端 post 发过来的 data)
app.use(express.json()); 

app.use('/uploads', express.static('uploads'));

// 解析 URL-encoded 格式的请求体
app.use(express.urlencoded({ extended: true }));


// ===========================
// 2. 路由配置 (Routes)
// ===========================

// 基础健康检查接口
app.get('/', (req, res) => {
    res.send({ 
        message: '🚀 AdFlux Backend is running!', 
        timestamp: new Date() 
    });
});

// 数据库连接测试接口 (方便你确认数据库真的通了)
app.get('/api/test-db', async (req, res) => {
    try {
        const [rows] = await db.query('SELECT 1 + 1 AS result');
        res.json({ 
            status: 'success', 
            message: 'Database connection verified', 
            result: rows[0].result 
        });
    } catch (error) {
        console.error('Database query failed:', error);
        res.status(500).json({ 
            status: 'error', 
            message: 'Database connection failed', 
            error: error.message 
        });
    }
});

// TODO: 之后我们会在这里引入广告路由
const adRoutes = require('./routes/ads');
app.use('/api/ads', adRoutes);


// ===========================
// 3. 全局错误处理
// ===========================
app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).send({ error: 'Something broke!', details: err.message });
});


// ===========================
// 4. 启动服务器
// ===========================
app.listen(PORT, () => {
    console.log(`\n===================================`);
    console.log(`🚀 Server running on port ${PORT}`);
    console.log(`🔗 Local: http://localhost:${PORT}`);
    console.log(`===================================\n`);
});