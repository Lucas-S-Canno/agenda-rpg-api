-- Adiciona a coluna jogadores na tabela eventos
ALTER TABLE eventos
    ADD COLUMN jogadores TEXT[] NOT NULL;