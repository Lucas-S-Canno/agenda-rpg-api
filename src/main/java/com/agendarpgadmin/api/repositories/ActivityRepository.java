package com.agendarpgadmin.api.repositories;
import java.util.UUID;

import com.agendarpgadmin.api.entities.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    List<ActivityEntity> findByEventoId(Long eventoId);
    List<ActivityEntity> findByNarradorId(Long narradorId);
    List<ActivityEntity> findByPalestranteId(Long palestranteId);
}

