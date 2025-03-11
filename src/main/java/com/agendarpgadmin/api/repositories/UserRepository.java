package com.agendarpgadmin.api.repositories;

import com.agendarpgadmin.api.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findByEmail(String email);
    List<UserEntity> findByTipoIn(List<String> tipos);
}
