package com.media.news.model;

public class NewsArticle {
    private String title;
    private String content;
    private String category; // 核心：分类 (e.g., "tech", "sports", "fashion")
    private String author;
    private String date;

    public NewsArticle(String title, String content, String category, String author, String date) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.author = author;
        this.date = date;
    }

    // Getters
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
}