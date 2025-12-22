// File: com/example/videowebplatform/controller/StreamServlet.java (最终版本)
package com.example.videowebplatform.controller;

import com.example.videowebplatform.dao.AdVideoDAO; // 【新增导入】
import com.example.videowebplatform.dao.AdVideoDAOImpl; // 【新增导入】
import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.model.AdVideo; // 【新增导入】
import com.example.videowebplatform.model.Video;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@WebServlet("/stream")
public class StreamServlet extends HttpServlet {

    // 【配置项】
    private static final String BASE_VIDEO_PATH = "E:/videos/";
    private final VideoDAO videoDAO = new VideoDAOImpl(); // 主视频DAO [cite: 16]
    private final AdVideoDAO adVideoDAO = new AdVideoDAOImpl(); // 【新增】广告视频DAO
    private static final int BUFFER_SIZE = 8192; // 8KB 缓冲区 [cite: 17]
    private final Random random = new Random();

    // 【广告配置】
    // 假设比特率 2Mbps / 8 = 250000 字节/秒。请根据您视频的实际编码比特率调整此值。
    private static final long BYTES_PER_SECOND = 2000000 / 8;
    private static List<AdVideo> AD_LIST; // 广告列表缓存

    // 广告位置常量
    private static final String AD_TYPE_PRE = "PRE";
    private static final String AD_TYPE_MID = "MID";
    private static final String AD_TYPE_POST = "POST";
    private static final String AD_TYPE_NONE = "NONE";

    // 内部类，用于存储本次请求的广告计划
    private class AdPlan {
        String type = AD_TYPE_NONE;
        long insertionByteOffset = 0;
        long totalVirtualLength = 0;
        AdVideo adVideo = null; // 本次选择的广告对象
    }

    @Override
    public void init() {
        // 在 Servlet 初始化时加载所有广告视频信息
        AD_LIST = adVideoDAO.getAllAds();
        System.out.println("========== 广告系统初始化 ==========");
        System.out.println("广告DAO类: " + adVideoDAO.getClass().getName());
        System.out.println("广告列表是否为空: " + (AD_LIST == null ? "是" : "否"));

        if (AD_LIST == null) {
            AD_LIST = new java.util.ArrayList<>();
            System.out.println("已将广告列表初始化为空列表");
        }

        System.out.println("广告数量: " + AD_LIST.size());

        for (int i = 0; i < AD_LIST.size(); i++) {
            AdVideo ad = AD_LIST.get(i);
            System.out.println("广告 #" + (i+1) + ":");
            System.out.println("  ID: " + ad.getId());
            System.out.println("  标题: " + ad.getTitle());
            System.out.println("  文件名: " + ad.getFileName());
            System.out.println("  时长: " + ad.getDurationSeconds() + "秒");
            System.out.println("  大小: " + ad.getFileLengthBytes() + "字节");

            // 检查文件是否存在
            String fileName = ad.getFileName();
            if (fileName != null && !fileName.trim().isEmpty()) {
                File adFile = new File(BASE_VIDEO_PATH + fileName);
                System.out.println("  文件路径: " + adFile.getAbsolutePath());
                System.out.println("  文件存在: " + adFile.exists());
            } else {
                System.out.println("  警告: 文件名为空！");
            }
            System.out.println();
        }
        System.out.println("==================================");
        if (AD_LIST == null) {
            AD_LIST = new java.util.ArrayList<>();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String videoIdParam = request.getParameter("id");
        if (videoIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Video mainVideo = videoDAO.getVideoById(Integer.parseInt(videoIdParam));

        // 检查主视频是否加载成功
        if (mainVideo == null || mainVideo.getFileName() == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // --- 核心：生成随机广告计划并计算虚拟文件总长度 ---
        AdPlan adPlan = generateRandomAdPlan(mainVideo);

        // 【Range Header 解析和响应头设置】
        long fileLength = adPlan.totalVirtualLength;
        long start = 0;
        long end = fileLength - 1;
        String rangeHeader = request.getHeader("Range");

        if (rangeHeader != null) {
           response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            String range = rangeHeader.substring("bytes=".length());
           int separatorIndex = range.indexOf("-");
            try {
                start = Long.parseLong(range.substring(0, separatorIndex));
                if (separatorIndex < range.length() - 1) {
                    end = Long.parseLong(range.substring(separatorIndex + 1));
                }
            } catch (NumberFormatException e) {
                 response.setHeader("Content-Range", "bytes */" + fileLength);
                 response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
        } else {
                response.setStatus(HttpServletResponse.SC_OK);
        }

        if (end < start || start < 0 || end >= fileLength) {
            response.setHeader("Content-Range", "bytes */" + fileLength);
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }

        // 设置响应头
        long contentLength = end - start + 1;
        response.setHeader("Content-Type", "video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileLength));

        // 4. 读取文件并输出流 (使用拼接逻辑)
        try (OutputStream outputStream = response.getOutputStream()) {
            long currentVirtualPos = start;
            long bytesRemaining = end - start + 1;

            while (bytesRemaining > 0) {
                Map<String, Object> segmentMap = mapVirtualPosition(
                        currentVirtualPos, mainVideo, adPlan
                );

                File actualFile = (File) segmentMap.get("file");
                long actualStart = (long) segmentMap.get("actualStart");
                long segmentLength = (long) segmentMap.get("segmentLength");

                if (actualFile == null) break;

                long bytesToRead = Math.min(bytesRemaining, segmentLength);

                streamSegment(outputStream, actualFile, actualStart, bytesToRead);

                currentVirtualPos += bytesToRead;
                bytesRemaining -= bytesToRead;
            }
            outputStream.flush();
        } catch (IOException e) {
             System.out.println("客户端连接中断或关闭。");
        }
    }

    /**
     * 【新增方法】生成随机广告计划 (随机决定插入位置)
     */
    private AdPlan generateRandomAdPlan(Video mainVideo) {
        System.out.println("\n=== 生成广告计划 ===");
        System.out.println("主视频: " + mainVideo.getTitle() + " (ID: " + mainVideo.getId() + ")");
        System.out.println("主视频文件: " + mainVideo.getFileName());
        System.out.println("主视频大小: " + mainVideo.getFileLengthBytes() + " bytes");

    AdPlan plan = new AdPlan();

    // 1. 检查是否有广告可用
    if (AD_LIST.isEmpty()) {
        System.out.println("警告: 广告列表为空，不插入广告");
        plan.type = AD_TYPE_NONE;
        plan.totalVirtualLength = mainVideo.getFileLengthBytes();
        return plan;
    }

    // 随机选择一个广告对象
    int adIndex = random.nextInt(AD_LIST.size());
    plan.adVideo = AD_LIST.get(adIndex);
    System.out.println("选择的广告: " + plan.adVideo.getTitle());
    System.out.println("广告文件: " + plan.adVideo.getFileName());
    System.out.println("广告大小: " + plan.adVideo.getFileLengthBytes() + " bytes");

    long mainLen = mainVideo.getFileLengthBytes();
    long adLen = plan.adVideo.getFileLengthBytes();

    // 2. 随机选择位置 (0: NONE, 1: PRE, 2: MID, 3: POST)
    int choice = random.nextInt(4);
    System.out.println("随机数结果: " + choice + " (0=NONE, 1=PRE, 2=MID, 3=POST)");

    if (choice == 1) {
        plan.type = AD_TYPE_PRE;
        plan.totalVirtualLength = mainLen + adLen;
        System.out.println("广告类型: 前贴片 (PRE)");
    } else if (choice == 2) {
        plan.type = AD_TYPE_MID;
        System.out.println("广告类型: 中插 (MID)");

        long safePadding = (long)(mainLen * 0.1);
        long minOffset = safePadding;
        long maxOffset = mainLen - safePadding - adLen;

        if (maxOffset > minOffset) {
            plan.insertionByteOffset = minOffset + random.nextInt((int)(maxOffset - minOffset));
            plan.totalVirtualLength = mainLen + adLen;
            System.out.println("插入位置: " + plan.insertionByteOffset + " bytes");
        } else {
            System.out.println("警告: 主视频太短无法中插，回退到无广告");
            plan.type = AD_TYPE_NONE;
            plan.totalVirtualLength = mainLen;
            plan.adVideo = null;
        }
    } else if (choice == 3) {
        plan.type = AD_TYPE_POST;
        plan.totalVirtualLength = mainLen + adLen;
        System.out.println("广告类型: 后贴片 (POST)");
    } else { // choice == 0
        plan.type = AD_TYPE_NONE;
        plan.totalVirtualLength = mainLen;
        plan.adVideo = null;
        System.out.println("广告类型: 无广告 (NONE)");
    }

    System.out.println("虚拟总长度: " + plan.totalVirtualLength + " bytes");
    System.out.println("=== 广告计划生成完毕 ===\n");

    return plan;
}


    /**
     * 【核心方法】根据广告计划映射虚拟字节位置到实际文件信息。
     * @param virtualPos 客户端请求的虚拟字节位置
     * @param mainVideo 主视频信息
     * @param adPlan 本次请求的广告计划
     * @return 包含实际文件、起始字节和段长度的 Map
     */
    private Map<String, Object> mapVirtualPosition(long virtualPos, Video mainVideo, AdPlan adPlan) {
        AdVideo adVideo = adPlan.adVideo;
        long adLen = (adVideo != null) ? adVideo.getFileLengthBytes() : 0;

        long mainLen = mainVideo.getFileLengthBytes();

        File mainFile = new File(BASE_VIDEO_PATH + mainVideo.getFileName());
        File adFile = (adVideo != null) ? new File(BASE_VIDEO_PATH + adVideo.getFileName()) : null;

        if (adPlan.type.equals(AD_TYPE_NONE)) {
            // 1. 无广告
            long segmentRemaining = mainLen - virtualPos;
            if (virtualPos < mainLen) {
                return createMap(mainFile, virtualPos, segmentRemaining);
            }
            return createMap(null, 0, 0);
        }

        // 确保广告文件存在
        if (adFile == null) {
            // 如果计划要求插广告但广告文件对象缺失，回退到 NONE 模式
            return mapVirtualPosition(virtualPos, mainVideo, new AdPlan());
        }

        switch (adPlan.type) {
            case AD_TYPE_PRE:
                // 2. 前贴片广告: [Ad] [Main]
                if (virtualPos < adLen) {
                    return createMap(adFile, virtualPos, adLen - virtualPos);
                } else {
                    long actualStart = virtualPos - adLen;
                    long segmentRemaining = mainLen - actualStart;
                    if (actualStart < mainLen) {
                        return createMap(mainFile, actualStart, segmentRemaining);
                    }
                }
                break;

            case AD_TYPE_POST:
                // 3. 后贴片广告: [Main] [Ad]
                if (virtualPos < mainLen) {
                    return createMap(mainFile, virtualPos, mainLen - virtualPos);
                } else {
                    long actualStart = virtualPos - mainLen;
                    long segmentRemaining = adLen - actualStart;
                    if (actualStart < adLen) {
                        return createMap(adFile, actualStart, segmentRemaining);
                    }
                }
                break;

            case AD_TYPE_MID:
                // 4. 中插广告: [Main A] [Ad] [Main B]
                long offset = adPlan.insertionByteOffset;

                if (virtualPos < offset) {
                    return createMap(mainFile, virtualPos, offset - virtualPos);
                } else if (virtualPos < offset + adLen) {
                    long actualStart = virtualPos - offset;
                    long segmentRemaining = offset + adLen - virtualPos;
                    return createMap(adFile, actualStart, segmentRemaining);
                } else {
                    long actualStart = virtualPos - adLen;
                    long segmentRemaining = mainLen - actualStart;
                    if (actualStart < mainLen) {
                        return createMap(mainFile, actualStart, segmentRemaining);
                    }
                }
                break;
        }

        return createMap(null, 0, 0);

    }

    // 辅助方法：用于封装映射结果
    private Map<String, Object> createMap(File file, long actualStart, long segmentLength) {
        Map<String, Object> map = new HashMap<>();
        map.put("file", file);
        map.put("actualStart", actualStart);
        map.put("segmentLength", segmentLength);
        return map;
    }

    /**
     * 辅助方法：处理单个主视频流（作为退回机制）
     */
    private void handleSingleVideoStream(HttpServletRequest request, HttpServletResponse response, Video video)
            throws IOException {

        if (video == null || video.getFileName() == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File videoFile = new File(BASE_VIDEO_PATH + video.getFileName());
        if (!videoFile.exists() || videoFile.isDirectory()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long fileLength = videoFile.length();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(fileLength));

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(videoFile));
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
             System.out.println("客户端连接中断或关闭。");
        }
    }

    /**
     * 辅助方法：从文件中读取指定字节段并输出流
     */
    private void streamSegment(OutputStream outputStream, File file, long start, long length) throws IOException {
        if (file == null || length <= 0) return;

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            inputStream.skip(start);

            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesRemaining = length;

            while (bytesRemaining > 0) {
                int bytesToRead = (int) Math.min(BUFFER_SIZE, bytesRemaining);
                int bytesRead = inputStream.read(buffer, 0, bytesToRead);

                if (bytesRead == -1) break;

                outputStream.write(buffer, 0, bytesRead);
                bytesRemaining -= bytesRead;
            }
        }
    }
}