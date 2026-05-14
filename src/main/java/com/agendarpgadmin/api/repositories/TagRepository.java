package com.agendarpgadmin.api.repositories;
import java.util.UUID;

import com.agendarpgadmin.api.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TagRepository extends JpaRepository<TagEntity, UUID>, JpaSpecificationExecutor<TagEntity> {
}
