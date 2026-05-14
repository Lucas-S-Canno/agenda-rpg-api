package com.agendarpgadmin.api.entities;

import com.agendarpgadmin.api.services.utils.UuidUtils;
import com.agendarpgadmin.api.entities.enums.ActivityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "atividades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEntity {

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
    @JoinColumn(name = "evento_id", nullable = false)
    private EventEntity evento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private ActivityType tipo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fim", nullable = false)
    private LocalDateTime fim;

    @Column(name = "local_complemento", nullable = false)
    private String localComplemento;

    @Column(name = "sistema")
    private String sistema;

    @Column(name = "numero_vagas")
    private Integer numeroVagas;

    @Column(name = "tags")
    private String tags;

    @Column(name = "narrador_id")
    private UUID narradorId;

    @Column(name = "tema")
    private String tema;

    @Column(name = "palestrante_id")
    private UUID palestranteId;
}

