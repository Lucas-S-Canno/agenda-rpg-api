package com.agendarpgadmin.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "sistema", nullable = false)
    private String sistema;

    @Column(name = "horario", nullable = false)
    private String horario;

    @Column(name = "numero_de_vagas", nullable = false)
    private int numeroDeVagas;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @ElementCollection
    @CollectionTable(name = "evento_tags", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "narrador", nullable = false)
    private String narrador;

    @ElementCollection
    @CollectionTable(name = "evento_jogadores", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "jogador")
    private List<String> jogadores;
}
