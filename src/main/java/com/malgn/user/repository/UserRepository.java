package com.malgn.user.repository;

import com.malgn.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserid(String userid);
    boolean existsByUserid(String userid);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
