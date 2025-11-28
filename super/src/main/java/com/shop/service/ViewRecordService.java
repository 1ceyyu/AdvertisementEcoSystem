package com.shop.service;

import com.shop.entity.ViewRecord;

import java.util.List;

public interface ViewRecordService {
    // 增加或更新浏览记录
    void addOrUpdateViewRecord(int userId, int categoryId);
    
    // 获取用户的浏览记录
    List<ViewRecord> findViewRecordsByUserId(int userId);
    
    // 获取所有浏览记录
    List<ViewRecord> findAllViewRecords();
}