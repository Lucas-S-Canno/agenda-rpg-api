package com.agendarpgadmin.api.dtos;
import java.util.UUID;

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
    private UUID id;
    private String nome;
    private String local;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private UUID creatorUserId;
    private List<ActivityDTO> atividades = new ArrayList<>();

}
