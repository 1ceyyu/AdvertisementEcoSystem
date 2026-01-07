package com.example.shopping.service;

import com.example.shopping.entity.AdDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdService {

    private final String AD_API_URL = "http://175.24.232.219:8080/api/ads";

    /**
     * 获取广告策略
     * @param userInterestCategory 用户当前最感兴趣的分类（如果没有则为 "暂无"）
     * @return Map 包含 "left" (精准推荐) 和 "right" (随机推荐) 两个列表
     */
    public Map<String, List<AdDto>> getAdStrategy(String userInterestCategory) {
        Map<String, List<AdDto>> result = new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        try {
            // 1. 获取并初步清洗数据 (只留图片，修复URL)
            AdDto[] response = restTemplate.getForObject(AD_API_URL, AdDto[].class);

            // 防止 API 挂掉导致空指针
            if (response == null) {
                result.put("left", Collections.emptyList());
                result.put("right", Collections.emptyList());
                return result;
            }

            // 过滤图片类型 + 修复 IP 端口
            List<AdDto> allImageAds = Arrays.stream(response)
                    .filter(ad -> "image".equalsIgnoreCase(ad.getType()))
                    .peek(ad -> {
                        if (ad.getMediaUrl() != null && ad.getMediaUrl().contains("175.24.232.219")) {
                            ad.setMediaUrl(ad.getMediaUrl().replace("175.24.232.219", "175.24.232.219:8080"));
                        }
                    })
                    .collect(Collectors.toList());

            // 2. 左侧广告逻辑：【精准投放】
            // 只有当用户有感兴趣的分类，且该分类有对应广告时，才填充左侧
            List<AdDto> leftAds = new ArrayList<>();
            if (userInterestCategory != null && !"暂无".equals(userInterestCategory)) {
                leftAds = allImageAds.stream()
                        .filter(ad -> userInterestCategory.equals(ad.getCategory()))
                        .collect(Collectors.toList());
                Collections.shuffle(leftAds); // 打乱顺序
            }

            // 3. 右侧广告逻辑：【随机投放】
            // 直接使用所有广告，打乱顺序
            List<AdDto> rightAds = new ArrayList<>(allImageAds);
            Collections.shuffle(rightAds);

            // 4. 封装结果 (限制数量，比如各显示2个)
            result.put("left", leftAds.stream().limit(2).collect(Collectors.toList()));
            result.put("right", rightAds.stream().limit(2).collect(Collectors.toList()));

        } catch (Exception e) {
            e.printStackTrace();
            result.put("left", Collections.emptyList());
            result.put("right", Collections.emptyList());
        }
        return result;
    }
}