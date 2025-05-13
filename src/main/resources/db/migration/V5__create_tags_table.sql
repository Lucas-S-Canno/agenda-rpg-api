-- Criação da tabela tags
CREATE TABLE tags (
                         id SERIAL PRIMARY KEY,                             -- ID único para cada tag
                         tag VARCHAR(255) NOT NULL                          -- Nome da tag
);