package com.agendarpgadmin.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordWithCodeDTO {
    private String novaSenha;
    private String confirmacaoNovaSenha;
    private String tokenVerificacao;
}

