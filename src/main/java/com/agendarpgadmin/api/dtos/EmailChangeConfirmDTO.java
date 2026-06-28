package com.agendarpgadmin.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailChangeConfirmDTO {
    private String novoEmail;
    private String tokenVerificacao;
}

