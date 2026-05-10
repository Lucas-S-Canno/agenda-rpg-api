package com.agendarpgadmin.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id", nullable = false)
    private ActivityEntity atividade;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

