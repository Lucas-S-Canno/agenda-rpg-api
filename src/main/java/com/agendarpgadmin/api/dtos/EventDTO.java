package com.agendarpgadmin.api.dtos;

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
public class EventDTO {
    private Long id;
    private String nome;
    private String local;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private Long creatorUserId;
    private List<ActivityDTO> atividades = new ArrayList<>();

}
