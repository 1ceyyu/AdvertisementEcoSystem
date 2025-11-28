package com.shop.service;

import com.shop.entity.User;

public interface UserService {
    // 用户登录
    User login(String username, String password);
    
    // 根据ID查找用户
    User findById(int id);
    
    // 添加用户
    void addUser(User user);
    
    // 更新用户
    void updateUser(User user);
    
    // 删除用户
    void deleteUser(int id);
}