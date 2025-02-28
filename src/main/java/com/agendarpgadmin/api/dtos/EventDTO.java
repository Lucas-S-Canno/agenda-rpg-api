package com.agendarpgadmin.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String titulo;
    private String sistema;
    private String horario;
    private int numeroDeVagas;
    private String descricao;
    private List<String> tags;
    private String narrador;
    private List<String> jogadores;
}
