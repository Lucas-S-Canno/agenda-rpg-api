package com.agendarpgadmin.api.dtos;

import com.agendarpgadmin.api.entities.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
    private Long id;
    private Long eventoId;
    private ActivityType tipo;
    private String nome;
    private String descricao;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String localComplemento;

    // RPG
    private String sistema;
    private Integer numeroVagas;
    private List<String> tags = new ArrayList<>();
    private Long narradorId;

    // Workshop
    private String tema;
    private Long palestranteId;

    private List<Long> participantes = new ArrayList<>();
}

