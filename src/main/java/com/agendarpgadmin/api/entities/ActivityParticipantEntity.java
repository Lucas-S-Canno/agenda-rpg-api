package com.agendarpgadmin.api.entities;

import com.agendarpgadmin.api.services.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "atividade_participantes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"atividade_id", "usuario_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipantEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UuidUtils.generateV7();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id", nullable = false)
    private ActivityEntity atividade;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

