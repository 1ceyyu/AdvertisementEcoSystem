<%-- File: /WEB-INF/views/videoPlayerNew.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${video.title} - 播放</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        /* 广告容器样式 */
        #adOverlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.95);
            z-index: 1000;
            display: none;
            justify-content: center;
            align-items: center;
        }

        #adPlayer {
            width: 80%;
            max-width: 900px;
            background: #000;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 5px 30px rgba(0,0,0,0.5);
        }

        #adVideo {
            width: 100%;
            height: auto;
            display: block;
        }

        .ad-controls {
            padding: 15px;
            background: #222;
            color: white;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        #skipAdBtn {
            background: #ff4444;
            color: white;
            border: none;
            padding: 8px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
        }

        #skipAdBtn:disabled {
            background: #666;
            cursor: not-allowed;
        }

        .ad-info {
            font-size: 14px;
            color: #aaa;
        }

        /* 主播放器样式 */
        .player-container {
            max-width: 1000px;
            margin: 20px auto;
            padding: 0 20px;
        }

        .video-title {
            margin-bottom: 20px;
            color: #333;
        }

        #mainVideo {
            width: 100%;
            height: auto;
            background: #000;
            border-radius: 8px;
        }

        .ad-notice {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            color: #856404;
            padding: 10px 15px;
            border-radius: 5px;
            margin: 15px 0;
            display: none;
        }

        .ad-notice.show {
            display: block;
        }
    </style>
</head>
<body>
<div class="player-container">
    <h1 class="video-title">${video.title}</h1>

    <!-- 广告提示 -->
    <div id="adNotice" class="ad-notice">
        <strong>📺 广告提示：</strong>
        <span id="adMessage">即将播放广告</span>
        <span id="countdown" style="margin-left: 10px;"></span>
    </div>

    <!-- 主视频播放器 -->
    <video id="mainVideo" controls preload="metadata">
        <source src="${pageContext.request.contextPath}/stream?id=${video.id}" type="video/mp4">
        您的浏览器不支持 HTML5 video 标签。
    </video>

    <!-- 广告播放器（覆盖层） -->
    <c:if test="${showAd}">
        <div id="adOverlay">
            <div id="adPlayer">
                <video id="adVideo" preload="auto">
                    <source src="${pageContext.request.contextPath}/resources/videos/ads/${adVideo.fileName}" type="video/mp4">
                </video>
                <div class="ad-controls">
                    <div class="ad-info">
                        广告: ${adVideo.title} (${adVideo.durationSeconds}秒)
                        <span id="adCountdown">广告剩余: <span id="adTime">${adVideo.durationSeconds}</span>秒</span>
                    </div>
                    <button id="skipAdBtn" disabled>
                        跳过广告 (<span id="skipSeconds">5</span>)
                    </button>
                </div>
            </div>
        </div>
    </c:if>

    <!-- 返回按钮 -->
    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/home" class="back-btn">← 返回视频列表</a>
    </div>
</div>

<!-- JavaScript -->
<script>
    // 广告相关变量
    <c:if test="${showAd}">
    var adType = ${adType}; // 0=前贴片，1=中插，2=后贴片
    var midAdTime = ${midAdTime}; // 中插时间点（秒）
    var adDuration = ${adVideo.durationSeconds}; // 广告时长
    var skipDelay = 5; // 5秒后可跳过
    var adPlayed = false; // 广告是否已播放

    // 广告视频元素
    var adOverlay = document.getElementById('adOverlay');
    var adVideo = document.getElementById('adVideo');
    var skipAdBtn = document.getElementById('skipAdBtn');
    var skipSecondsSpan = document.getElementById('skipSeconds');
    var adTimeSpan = document.getElementById('adTime');
    var adNotice = document.getElementById('adNotice');
    var adMessage = document.getElementById('adMessage');
    var countdownSpan = document.getElementById('countdown');

    // 主视频元素
    var mainVideo = document.getElementById('mainVideo');
    </c:if>

    // 页面加载完成后初始化
    document.addEventListener('DOMContentLoaded', function() {
        <c:if test="${showAd}">
        // 根据广告类型设置不同的处理逻辑
        switch(adType) {
            case 0: // 前贴片广告
                showAdNotice('视频前有广告，请稍候...');
                setTimeout(playPreRollAd, 1000); // 1秒后显示广告
                break;

            case 1: // 中插广告
                setupMidRollAd();
                break;

            case 2: // 后贴片广告
                setupPostRollAd();
                break;
        }
        </c:if>
    });

    <c:if test="${showAd}">
    // ========== 前贴片广告 ==========
    function playPreRollAd() {
        console.log('播放前贴片广告');
        mainVideo.pause();
        showAdOverlay();
        startAdTimer();
    }

    // ========== 中插广告 ==========
    function setupMidRollAd() {
        console.log('设置中插广告，时间点:', midAdTime, '秒');

        mainVideo.addEventListener('timeupdate', function() {
            if (!adPlayed && this.currentTime >= midAdTime) {
                console.log('达到中插时间点，播放广告');
                this.pause();
                showAdNotice('广告时间，请稍候...');
                setTimeout(playMidRollAd, 1000);
            }
        });
    }

    function playMidRollAd() {
        console.log('播放中插广告');
        showAdOverlay();
        startAdTimer();
    }

    // ========== 后贴片广告 ==========
    function setupPostRollAd() {
        console.log('设置后贴片广告');

        mainVideo.addEventListener('ended', function() {
            console.log('视频播放结束，播放后贴片广告');
            showAdNotice('广告时间，请稍候...');
            setTimeout(playPostRollAd, 1000);
        });
    }

    function playPostRollAd() {
        console.log('播放后贴片广告');
        showAdOverlay();
        startAdTimer();
    }

    // ========== 通用广告函数 ==========
    function showAdOverlay() {
        adOverlay.style.display = 'flex';
        adVideo.currentTime = 0;
        adVideo.play().catch(e => console.log('广告播放失败:', e));
        adPlayed = true;

        // 隐藏广告提示
        adNotice.classList.remove('show');
    }

    function hideAdOverlay() {
        adOverlay.style.display = 'none';
        adVideo.pause();
        adVideo.currentTime = 0;

        // 根据广告类型恢复主视频
        if (adType === 0) { // 前贴片广告结束后播放主视频
            mainVideo.play();
        } else if (adType === 1) { // 中插广告结束后继续播放主视频
            mainVideo.play();
        }
        // 后贴片广告结束后不需要操作
    }

    function showAdNotice(message) {
        adMessage.textContent = message;
        adNotice.classList.add('show');

        // 显示倒计时
        var countdown = 3;
        countdownSpan.textContent = countdown + '秒后播放';

        var timer = setInterval(function() {
            countdown--;
            if (countdown > 0) {
                countdownSpan.textContent = countdown + '秒后播放';
            } else {
                clearInterval(timer);
                countdownSpan.textContent = '';
            }
        }, 1000);
    }

    function startAdTimer() {
        var skipSeconds = skipDelay;
        var adRemaining = adDuration;

        // 更新跳过按钮
        skipSecondsSpan.textContent = skipSeconds;
        skipAdBtn.disabled = true;

        // 更新广告剩余时间
        adTimeSpan.textContent = adRemaining;

        // 每秒更新一次
        var timer = setInterval(function() {
            // 更新跳过倒计时
            if (skipSeconds > 0) {
                skipSeconds--;
                skipSecondsSpan.textContent = skipSeconds;
                if (skipSeconds === 0) {
                    skipAdBtn.disabled = false;
                    skipAdBtn.textContent = '跳过广告';
                }
            }

            // 更新广告剩余时间
            if (adRemaining > 0) {
                adRemaining--;
                adTimeSpan.textContent = adRemaining;
            }

            // 检查广告是否自然结束
            if (adVideo.ended || adRemaining <= 0) {
                clearInterval(timer);
                hideAdOverlay();
            }
        }, 1000);

        // 监听广告结束事件
        adVideo.addEventListener('ended', function() {
            clearInterval(timer);
            hideAdOverlay();
        });

        // 跳过按钮事件
        skipAdBtn.onclick = function() {
            if (!skipAdBtn.disabled) {
                clearInterval(timer);
                hideAdOverlay();
            }
        };
    }
    </c:if>

    // 无广告时的处理
    <c:if test="${not showAd}">
    console.log('本次播放无广告');
    document.addEventListener('DOMContentLoaded', function() {
        var mainVideo = document.getElementById('mainVideo');
        mainVideo.play(); // 直接播放
    });
    </c:if>
</script>
</body>
</html>