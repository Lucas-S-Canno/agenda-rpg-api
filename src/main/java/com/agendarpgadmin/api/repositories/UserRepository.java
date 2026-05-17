package com.agendarpgadmin.api.repositories;
import java.util.UUID;

import com.agendarpgadmin.api.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByTipoIn(List<String> tipos);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :password WHERE u.email = :email")
    void updatePasswordByEmail(String email, String password);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.emailVerified = true WHERE u.email = :email")
    void markEmailAsVerified(String email);
}
