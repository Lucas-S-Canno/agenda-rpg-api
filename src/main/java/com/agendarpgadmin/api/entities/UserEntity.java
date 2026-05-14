package com.agendarpgadmin.api.entities;

import com.agendarpgadmin.api.services.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Explicit getters/setters for Kotlin interop
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UuidUtils.generateV7();
        }
    }
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
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
}