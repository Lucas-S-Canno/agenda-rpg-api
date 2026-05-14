package com.agendarpgadmin.api.repositories;
import java.util.UUID;

import com.agendarpgadmin.api.entities.ActivityParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipantEntity, UUID> {
    List<ActivityParticipantEntity> findByAtividadeId(UUID atividadeId);
    List<ActivityParticipantEntity> findByUsuarioId(UUID usuarioId);
    boolean existsByAtividadeIdAndUsuarioId(UUID atividadeId, UUID usuarioId);
    long countByAtividadeId(UUID atividadeId);
    void deleteByAtividadeIdAndUsuarioId(UUID atividadeId, UUID usuarioId);
}

