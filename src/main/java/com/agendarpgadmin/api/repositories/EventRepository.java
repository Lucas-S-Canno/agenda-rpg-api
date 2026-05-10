package com.agendarpgadmin.api.repositories;

import com.agendarpgadmin.api.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    List<EventEntity> findByInicioGreaterThanEqualOrderByInicioAsc(LocalDateTime inicio);
    List<EventEntity> findAllByOrderByInicioDesc();
}
