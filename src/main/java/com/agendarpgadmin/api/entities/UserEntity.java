package com.agendarpgadmin.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "senha")
    private String password;
    @Column(name = "nome_completo")
    private String nomeCompleto;
    @Column(name = "data_de_nascimento")
    private String dataDeNascimento;
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "telefone")
    private String telefone;
    @Column(name = "menor")
    private String menor;
    @Column(name = "responsavel")
    private String responsavel;
    @Column(name = "telefone_responsavel")
    private String telefoneResponsavel;
    @Column(name = "apelido")
    private String apelido;
}