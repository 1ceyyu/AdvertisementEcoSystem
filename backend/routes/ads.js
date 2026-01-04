const express = require('express');
const router = express.Router();
const db = require('../config/db');

// 获取所有资源 API
router.get('/', async (req, res) => {
    try {
        // 1. 从数据库读取所有行
        const [rows] = await db.query('SELECT * FROM ads ORDER BY id DESC');
        
        // 2. 获取当前服务器的访问前缀 (例如 http://123.123.123.123:3001)
        const protocol = req.protocol;
        const host = req.get('host');
        const baseUrl = `${protocol}://${host}/uploads/`;

        // 3. 映射数据：如果 media_url 不是外链，就自动加上我们服务器的地址
        const data = rows.map(ad => {
            const isExternal = ad.media_url && (ad.media_url.startsWith('http') || ad.media_url.startsWith('//'));
            return {
                ...ad,
                // 如果是本地文件名，拼成完整 URL；如果是外链，保持原样
                media_url: isExternal ? ad.media_url : `${baseUrl}${ad.media_url}`
            };
        });

        res.json(data);
    } catch (error) {
        console.error("API Error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

module.exports = router;