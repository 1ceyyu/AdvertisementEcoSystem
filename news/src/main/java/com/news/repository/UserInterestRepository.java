package com.news.repository;

import com.news.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    // 查找某个用户对某个具体分类的记录（为了做累加）
    Optional<UserInterest> findByUserUuidAndCategory(String userUuid, String category);

    // 查找某用户的所有兴趣，按分数从高到低排序（为了做推荐）
    List<UserInterest> findByUserUuidOrderByScoreDesc(String userUuid);
}