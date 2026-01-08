package com.news.repository;

import com.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // 1. 根据分类查询 (倒序)
    List<News> findByCategoryOrderByCreateTimeDesc(String category);

    // 2. 模糊查询: 标题包含关键词 OR 内容包含关键词
    List<News> findByTitleContainingOrContentContainingOrderByCreateTimeDesc(String title, String content);

    // 3. 既有分类又有关键词 (管理员界面可能用到)
    List<News> findByCategoryAndTitleContainingOrderByCreateTimeDesc(String category, String title);
}