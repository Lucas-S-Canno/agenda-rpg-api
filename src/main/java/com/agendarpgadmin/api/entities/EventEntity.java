package com.agendarpgadmin.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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

    @Column(name = "narrador", nullable = false)
    private String narrador;

    @Column(name = "data", nullable = false)
    private String data;

    @Column(name = "local", nullable = false)
    private String local;

    @Column(name = "tags")
    private String tags;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "jogadores")
    private String jogadores;
}
