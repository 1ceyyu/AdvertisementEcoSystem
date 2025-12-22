
document.addEventListener('DOMContentLoaded', function() {
    console.log('视频网站已加载完毕！');

    // 示例：如果将来需要，可以在此添加视频播放器事件监听器
    const videoElement = document.querySelector('video');
    if (videoElement) {
        videoElement.addEventListener('play', function() {
            // console.log('视频开始播放');
            // 可以在此处发送 AJAX 请求记录观看次数
        });

        videoElement.addEventListener('error', function(e) {
            console.error('视频播放错误:', e);
            // 提示用户视频无法加载
            // alert('视频加载失败，请检查文件路径或网络连接。');
        });
    }

    // 示例：处理图片加载错误，使用默认封面
    document.querySelectorAll('.video-card img').forEach(img => {
        img.onerror = function() {
            this.onerror = null; // 防止无限循环
            this.src = '${pageContext.request.contextPath}/resources/covers/default.jpg'; // 假设您有一个默认封面
        };
    });
});