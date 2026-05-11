-- Participants registered in activities
CREATE TABLE IF NOT EXISTS atividade_participantes (
    id BIGSERIAL PRIMARY KEY,
    atividade_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_atividade_participantes_atividade FOREIGN KEY (atividade_id) REFERENCES atividades(id) ON DELETE CASCADE,
    CONSTRAINT uk_atividade_participante UNIQUE (atividade_id, usuario_id)
);

