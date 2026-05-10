-- Rename legacy table so the new event model can reuse the `eventos` name
ALTER TABLE IF EXISTS eventos RENAME TO eventos_legado;

