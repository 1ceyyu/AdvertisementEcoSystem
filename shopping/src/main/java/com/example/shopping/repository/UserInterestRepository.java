package com.example.shopping.repository;

import com.example.shopping.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    Optional<UserInterest> findByUserIdAndCategory(Long userId, String category);

    List<UserInterest> findByUserIdOrderByClickCountDesc(Long userId);

    // --- 新增：根据用户ID清空兴趣记录 ---
    void deleteByUserId(Long userId);
}