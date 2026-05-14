package com.agendarpgadmin.api.dtos;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NarratorProfileDTO {
    private UUID id;
    private String apelido;
    private String email;
}
