package com.shop.service;

import com.shop.dao.ViewRecordDAO;
import com.shop.dao.ViewRecordDAOImpl;
import com.shop.entity.ViewRecord;

import java.util.List;

public class ViewRecordServiceImpl implements ViewRecordService {
    private ViewRecordDAO viewRecordDAO = new ViewRecordDAOImpl();
    
    @Override
    public void addOrUpdateViewRecord(int userId, int categoryId) {
        viewRecordDAO.addOrUpdateRecord(userId, categoryId);
    }
    
    @Override
    public List<ViewRecord> findViewRecordsByUserId(int userId) {
        return viewRecordDAO.findByUserId(userId);
    }
    
    @Override
    public List<ViewRecord> findAllViewRecords() {
        return viewRecordDAO.findAll();
    }
}