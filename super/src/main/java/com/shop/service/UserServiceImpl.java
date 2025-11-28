package com.shop.service;

import com.shop.dao.UserDAO;
import com.shop.dao.UserDAOImpl;
import com.shop.entity.User;

public class UserServiceImpl implements UserService {
    private UserDAO userDAO = new UserDAOImpl();
    
    @Override
    public User login(String username, String password) {
        return userDAO.findByUsernameAndPassword(username, password);
    }
    
    @Override
    public User findById(int id) {
        return userDAO.findById(id);
    }
    
    @Override
    public void addUser(User user) {
        userDAO.add(user);
    }
    
    @Override
    public void updateUser(User user) {
        userDAO.update(user);
    }
    
    @Override
    public void deleteUser(int id) {
        userDAO.delete(id);
    }
}