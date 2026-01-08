package com.news.repository;

import com.news.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    // ✅ 关键方法：根据 UUID 查找该用户所有的行为记录
    List<UserBehavior> findByUserUuid(String userUuid);
    // ✅ 查找某用户最近的10条行为记录，按点击时间倒序排序
    List<UserBehavior> findTop10ByUserUuidOrderByClickTimeDesc(String userUuid);
}