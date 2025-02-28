-- Criação da tabela eventos
CREATE TABLE eventos (
                         id SERIAL PRIMARY KEY,                             -- ID único para cada evento
                         titulo VARCHAR(255) NOT NULL,                      -- Título do evento
                         sistema VARCHAR(255) NOT NULL,                     -- Sistema do evento
                         horario VARCHAR(255) NOT NULL,                     -- Horário do evento
                         numero_de_vagas INT NOT NULL,                      -- Número de vagas do evento
                         descricao TEXT NOT NULL,                           -- Descrição do evento
                         tags TEXT[] NOT NULL,                              -- Tags do evento (array de strings)
                         narrador VARCHAR(255) NOT NULL                     -- Narrador do evento
);