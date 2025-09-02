package com.agendarpgadmin.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    private String email;
    private String apelido;
    private String telefone;
    private String responsavel;
    private String telefoneResponsavel;
}
