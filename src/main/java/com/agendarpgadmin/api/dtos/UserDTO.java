package com.agendarpgadmin.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String nomeCompleto;
    private String dataDeNascimento;
    private String tipo;
    private String telefone;
    private String menor;
    private String responsavel;
    private String telefoneResponsavel;
    private String apelido;
}
