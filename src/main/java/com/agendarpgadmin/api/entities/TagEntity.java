package com.agendarpgadmin.api.entities;

import com.agendarpgadmin.api.services.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UuidUtils.generateV7();
        }
    }

    @Column(name = "tag", nullable = false)
    private String tag;
}
