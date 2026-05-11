package com.agendarpgadmin.api.repositories;

import com.agendarpgadmin.api.entities.ActivityParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipantEntity, Long> {
    List<ActivityParticipantEntity> findByAtividadeId(Long atividadeId);
    List<ActivityParticipantEntity> findByUsuarioId(Long usuarioId);
    boolean existsByAtividadeIdAndUsuarioId(Long atividadeId, Long usuarioId);
    long countByAtividadeId(Long atividadeId);
    void deleteByAtividadeIdAndUsuarioId(Long atividadeId, Long usuarioId);
}

