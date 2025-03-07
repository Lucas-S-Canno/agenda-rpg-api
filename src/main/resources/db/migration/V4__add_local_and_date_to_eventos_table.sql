-- Adiciona as colunas local e data ao evento
ALTER TABLE eventos
    ADD COLUMN local TEXT NOT NULL,
    ADD COLUMN data DATE NOT NULL;