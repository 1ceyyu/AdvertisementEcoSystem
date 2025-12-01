document.addEventListener("DOMContentLoaded", function() {
    // 1. 获取当前页面的分类 (从Meta标签)
    const categoryMeta = document.querySelector('meta[name="content-category"]');
    const category = categoryMeta ? categoryMeta.getAttribute("content") : "general";

    console.log(`[Media Site] Current Page Category: ${category}`);

    // 模拟广告服务器的地址 (假设广告系统部署在 localhost:8080/AdServer)
    // 注意：在实际跨域场景中，端口或域名会不同
    const AD_SERVER_URL = "http://localhost:8080/AdServer";

    /**
     * 核心点4：解决跨域问题与构建用户画像
     * 发送请求给广告服务器。
     *
     * 技术要点：
     * 1. mode: 'cors' - 允许跨域
     * 2. credentials: 'include' - 允许携带第三方Cookie (这是追踪匿名用户的关键)
     * 3. 发送 content_category - 告诉广告服务器用户正在看什么
     */

    // 步骤 A: 上报用户行为 (构建画像)
    // 实际生产中，这通常是一个 1x1 像素的图片请求 (Pixel Tracking) 或者 sendBeacon
    fetch(`${AD_SERVER_URL}/track`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        // 携带 Cookie，以便广告服务器通过 SessionID/CookieID 识别这是一个 "老用户"
        credentials: 'include',
        body: JSON.stringify({
            site_id: 'news_media_001',
            view_category: category,
            timestamp: new Date().getTime()
        })
    })
        .then(response => {
            console.log("[Media Site] User behavior tracked successfully.");
            // 行为上报成功后，或者并行地，请求广告
            loadTargetedAd(category);
        })
        .catch(err => console.error("[Media Site] Tracking failed:", err));


    // 步骤 B: 请求精准广告
    function loadTargetedAd(userCurrentInterest) {
        const adContainer = document.getElementById('ad-container');

        // 请求广告API
        fetch(`${AD_SERVER_URL}/getAd?category=${userCurrentInterest}`, {
            method: 'GET',
            credentials: 'include' // 同样携带Cookie，广告服务器可能根据历史画像调整推荐
        })
            .then(response => response.json()) // 假设返回 JSON
            .then(data => {
                // 渲染广告
                adContainer.innerHTML = `
                <div style="cursor:pointer;" onclick="window.open('${data.link}')">
                    <img src="${data.imageUrl}" style="max-width:100%; border-radius:4px;" alt="Ad">
                    <p style="font-size:12px; color:#666; margin-top:5px;">${data.title}</p>
                </div>
            `;
                adContainer.classList.remove('ad-placeholder'); // 移除占位样式
            })
            .catch(err => {
                // 失败时的兜底显示 (Mock展示，实际不需要管API实现)
                console.warn("Ad Server not connected. Showing mock ad.");
                mockAdDisplay(category, adContainer);
            });
    }

    // --- 仅用于演示效果的本地模拟函数 (当没有真实Ad Server后台时) ---
    function mockAdDisplay(cat, container) {
        let adContent = "";
        if (cat === 'technology') {
            adContent = "推荐购买：高性能机械键盘 (基于您对科技新闻的兴趣)";
        } else if (cat === 'sports') {
            adContent = "推荐购买：限量版运动跑鞋 (基于您对体育新闻的兴趣)";
        } else {
            adContent = "推荐理财：5%年化收益率基金 (基于您对财经新闻的兴趣)";
        }

        container.innerHTML = `
            <div style="background:#e3f2fd; height:100%; display:flex; flex-direction:column; justify-content:center; padding:10px;">
                <h4 style="color:#0d6efd;">精准广告位</h4>
                <p>${adContent}</p>
                <small>来自中心化广告平台的推送</small>
            </div>
        `;
        container.classList.remove('ad-placeholder');
    }
});