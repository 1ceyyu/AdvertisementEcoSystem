-- 创建数据库
CREATE DATABASE IF NOT EXISTS shop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'admin或customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品分类表
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 浏览记录表
CREATE TABLE IF NOT EXISTS view_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    view_count INT DEFAULT 1,
    last_viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    UNIQUE KEY uk_user_category (user_id, category_id)
);

-- 插入初始数据
-- 插入管理员用户
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin');

-- 插入测试消费者用户
INSERT INTO users (username, password, role) VALUES ('customer1', 'customer123', 'customer');

-- 插入商品分类
INSERT INTO categories (name, description) VALUES ('电子产品', '各类电子产品');
INSERT INTO categories (name, description) VALUES ('服装', '各类服装');
INSERT INTO categories (name, description) VALUES ('食品', '各类食品');
INSERT INTO categories (name, description) VALUES ('家居用品', '各类家居用品');

-- 插入商品
INSERT INTO products (name, description, price, category_id) VALUES ('手机', '智能手机', 2999.00, 1);
INSERT INTO products (name, description, price, category_id) VALUES ('电脑', '笔记本电脑', 5999.00, 1);
INSERT INTO products (name, description, price, category_id) VALUES ('T恤', '纯棉T恤', 99.00, 2);
INSERT INTO products (name, description, price, category_id) VALUES ('牛仔裤', '休闲牛仔裤', 199.00, 2);
INSERT INTO products (name, description, price, category_id) VALUES ('苹果', '新鲜苹果', 12.99, 3);
INSERT INTO products (name, description, price, category_id) VALUES ('面包', '全麦面包', 8.99, 3);
INSERT INTO products (name, description, price, category_id) VALUES ('台灯', 'LED台灯', 89.00, 4);
INSERT INTO products (name, description, price, category_id) VALUES ('枕头', '记忆棉枕头', 129.00, 4);