-- Activities happen inside events
CREATE TABLE IF NOT EXISTS atividades (
    id BIGSERIAL PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    local_complemento VARCHAR(255) NOT NULL,
    sistema VARCHAR(255),
    numero_vagas INTEGER,
    tags TEXT,
    narrador_id BIGINT,
    tema VARCHAR(255),
    palestrante_id BIGINT,
    CONSTRAINT fk_atividades_evento FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE
);

