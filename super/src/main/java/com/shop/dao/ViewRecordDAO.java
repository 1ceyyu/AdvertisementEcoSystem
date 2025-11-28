package com.shop.dao;

import com.shop.entity.ViewRecord;

import java.util.List;

public interface ViewRecordDAO {
    // 增加或更新浏览记录
    void addOrUpdateRecord(int userId, int categoryId);
    
    // 获取用户的浏览记录
    List<ViewRecord> findByUserId(int userId);
    
    // 获取所有浏览记录
    List<ViewRecord> findAll();
}