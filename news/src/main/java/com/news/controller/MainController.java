package com.news.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.news.entity.News;
import com.news.entity.UserBehavior;
import com.news.entity.UserInterest;
import com.news.repository.NewsRepository;
import com.news.repository.UserBehaviorRepository;
import com.news.repository.UserInterestRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final NewsRepository newsRepository;
    private final UserBehaviorRepository behaviorRepository;
    private final UserInterestRepository interestRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ================= 用户端路由 =================

    /**
     * 首页：支持展示全部、按分类筛选、按关键词搜索
     */
    @GetMapping("/")
    public String index(@RequestParam(required = false) String category,
                        @RequestParam(required = false) String keyword,
                        Model model) {
        List<News> newsList;

        if (keyword != null && !keyword.isEmpty()) {
            // 如果有搜索词，优先模糊查询
            newsList = newsRepository.findByTitleContainingOrContentContainingOrderByCreateTimeDesc(keyword, keyword);
            model.addAttribute("currentKeyword", keyword);
        } else if (category != null && !category.isEmpty()) {
            // 如果有分类，按分类查
            newsList = newsRepository.findByCategoryOrderByCreateTimeDesc(category);
            model.addAttribute("currentCategory", category);
        } else {
            // 否则查所有
            newsList = newsRepository.findAll();
        }

        model.addAttribute("newsList", newsList);
        return "index";
    }

    /**
     * 新闻详情页
     */
    @GetMapping("/news/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("新闻不存在"));
        model.addAttribute("news", news);
        return "detail";
    }

    // ================= 管理员路由 =================

    /**
     * 管理后台：支持搜索和筛选
     */
    @GetMapping("/admin")
    public String adminPage(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String keyword,
                            Model model) {
        List<News> newsList;
        if (keyword != null && !keyword.isEmpty()) {
            newsList = newsRepository.findByTitleContainingOrContentContainingOrderByCreateTimeDesc(keyword, keyword);
        } else if (category != null && !category.isEmpty()) {
            newsList = newsRepository.findByCategoryOrderByCreateTimeDesc(category);
        } else {
            newsList = newsRepository.findAll();
        }
        model.addAttribute("allNews", newsList);
        return "admin";
    }

    /**
     * 新增或修改新闻
     * 如果ID存在则是修改，不存在则是新增
     */
    @PostMapping("/admin/save")
    public String saveNews(News news) {
        newsRepository.save(news);
        return "redirect:/admin";
    }

    /**
     * 获取单条新闻用于编辑回显 (AJAX调用或页面跳转均可，这里用页面跳转简单点)
     * 为了简化，我们直接在 admin 页面里用 JS 填充表单，或者增加一个编辑页。
     * 这里演示最简单的：删除
     */
    @GetMapping("/admin/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsRepository.deleteById(id);
        return "redirect:/admin";
    }

    // ================= API 接口 =================

    /**
     * 记录用户行为
     */
    @PostMapping("/api/track")
    @ResponseBody
    public String trackUser(@RequestBody Map<String, String> payload) {
        String uuid = payload.get("uuid");
        String category = payload.get("category");

        if(uuid == null || category == null) return "error";

        UserBehavior behavior = new UserBehavior();
        behavior.setUserUuid(uuid);
        behavior.setCategory(category);
        behaviorRepository.save(behavior);

        // 2. 【积分】更新用户兴趣表 (UserInterest)
        // 先查一下这个用户对这个分类有没有记录
        UserInterest interest = interestRepository.findByUserUuidAndCategory(uuid, category)
                .orElse(new UserInterest()); // 如果没查到，就new一个新的

        if (interest.getUserUuid() == null) {
            // 如果是新的，初始化基本信息
            interest.setUserUuid(uuid);
            interest.setCategory(category);
            interest.setScore(1); // 第一次点击，得1分
        } else {
            // 如果已存在，分数 +1
            interest.setScore(interest.getScore() + 1);
        }
        interestRepository.save(interest); // 保存或更新

        System.out.println("积分更新: 用户 " + uuid + " [" + category + "] 当前分值: " + interest.getScore());
        return "success";
    }


    @GetMapping("/api/ad/recommend")
    @ResponseBody
    public String recommendAd(@RequestParam String uuid) {
        // 1. 获取所有广告 (保持不变)
        String friendsApiUrl = "http://175.24.232.219:8080/api/ads/";
        List<FriendAdDTO> allAds;
        try {
            String jsonResponse = restTemplate.getForObject(friendsApiUrl, String.class);
            allAds = objectMapper.readValue(jsonResponse, new TypeReference<List<FriendAdDTO>>() {});
        } catch (Exception e) {
            return getDefaultAdJson();
        }

        if (allAds == null || allAds.isEmpty()) return getDefaultAdJson();

        // 过滤出所有活跃广告，备用
        List<FriendAdDTO> activeAds = allAds.stream()
                .filter(ad -> ad.getIsActive() == 1)
                .collect(Collectors.toList());

        if (activeAds.isEmpty()) return getDefaultAdJson();

        // ============================================================
        // 核心算法修改：Epsilon-Greedy (70% 利用, 30% 探索)
        // ============================================================

        // 生成一个 0.0 到 1.0 之间的随机数
        double randomFactor = Math.random();

        // 阈值设为 0.7 (即 70% 的概率)
        if (randomFactor < 0.7) {
            // >>>>>>>>> 进入 70% 分支：基于兴趣推荐 (利用) <<<<<<<<<
            try {
                // 1. 查最近 10 条记录
                List<UserBehavior> recentBehaviors = behaviorRepository.findTop10ByUserUuidOrderByClickTimeDesc(uuid);

                if (!recentBehaviors.isEmpty()) {
                    // 2. 统计最高频分类
                    Map<String, Long> countMap = recentBehaviors.stream()
                            .map(UserBehavior::getCategory)
                            .collect(Collectors.groupingBy(java.util.function.Function.identity(), Collectors.counting()));

                    String bestCategory = countMap.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse(null);

                    // 3. 映射并筛选
                    if (bestCategory != null) {
                        String friendCategory = mapToFriendCategory(bestCategory);

                        List<FriendAdDTO> interestAds = activeAds.stream()
                                .filter(ad -> ad.getCategory() != null && ad.getCategory().equals(friendCategory))
                                .collect(Collectors.toList());

                        if (!interestAds.isEmpty()) {
                            System.out.println("[70% 算法命中] 基于兴趣推荐: " + bestCategory + " -> " + friendCategory);
                            return toJson(pickRandomAd(interestAds));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("兴趣计算出错，降级为随机推荐");
            }
        } else {
            // >>>>>>>>> 进入 30% 分支：强制随机推荐 (探索) <<<<<<<<<
            System.out.println("[30% 算法命中] 强制随机推荐 (探索新兴趣)");
        }

        // 兜底：如果是 30% 分支，或者 70% 分支里没算出来结果，都走这里随机选
        return toJson(pickRandomAd(activeAds));
    }

    /**
     * 【核心映射器】
     * 左边是你的 category (英文)，右边是你朋友 API 里的 category (中文)
     */
    private String mapToFriendCategory(String myCategory) {
        if (myCategory == null) return "";

        return switch (myCategory.toLowerCase()) {
            case "education" -> "大学";
            case "games" -> "游戏";
            case "tech" -> "AI";
            case "entertainment" -> "衣服";
            case "health" -> "美食";
            default -> "大学";       // 默认映射
        };
    }

    private FriendAdDTO pickRandomAd(List<FriendAdDTO> list) {
        if (list == null || list.isEmpty()) return null;
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return getDefaultAdJson();
        }
    }

    private String getDefaultAdJson() {
        return "{\"title\": \"默认广告\", \"media_url\": \"https://via.placeholder.com/300x200\", \"type\": \"image\"}";
    }

    /**
     * DTO 类：完全对应你朋友的 JSON 结构
     */
    @Data
    static class FriendAdDTO {
        private Long id;
        private String title;
        private String type;      // "image" or "video"

        @JsonProperty("media_url") // 确保 JSON 中的下划线能映射到 Java 驼峰 (或者直接用同名变量)
        private String mediaUrl;

        @JsonProperty("target_url")
        private String targetUrl;

        @JsonProperty("is_active")
        private Integer isActive; // 1 或 0

        private String category;  // "衣服", "大学", "AI" 等

        // created_at, views, clicks 不需要映射，除非你要用
    }
}