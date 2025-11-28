package com.shop.entity;

import java.util.Date;

public class ViewRecord {
    private int id;
    private int userId;
    private int categoryId;
    private int viewCount;
    private Date lastViewedAt;
    private User user;
    private Category category;
    
    // 构造方法
    public ViewRecord() {}
    
    public ViewRecord(int userId, int categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.viewCount = 1;
    }
    
    // getter和setter方法
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    public Date getLastViewedAt() {
        return lastViewedAt;
    }
    
    public void setLastViewedAt(Date lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
}