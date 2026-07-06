package com.erpbridge.repository;

import com.erpbridge.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAuthRepository
        extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}