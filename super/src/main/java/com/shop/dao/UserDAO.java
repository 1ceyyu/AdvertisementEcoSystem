package com.shop.dao;

import com.shop.entity.User;

public interface UserDAO {
    // 根据用户名和密码查找用户
    User findByUsernameAndPassword(String username, String password);
    
    // 根据ID查找用户
    User findById(int id);
    
    // 添加用户
    void add(User user);
    
    // 更新用户
    void update(User user);
    
    // 删除用户
    void delete(int id);
}