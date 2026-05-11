package com.agendarpgadmin.api.repositories;

import com.agendarpgadmin.api.entities.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {
    List<ActivityEntity> findByEventoId(Long eventoId);
    List<ActivityEntity> findByNarradorId(Long narradorId);
    List<ActivityEntity> findByPalestranteId(Long palestranteId);
}

