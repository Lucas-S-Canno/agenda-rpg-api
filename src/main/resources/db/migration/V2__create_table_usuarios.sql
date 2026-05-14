-- Criação da tabela usuarios
CREATE TABLE usuarios (
                          id UUID PRIMARY KEY,                               -- ID único (UUID v7)
                          email VARCHAR(255) NOT NULL UNIQUE,                -- Email único do usuário
                          senha VARCHAR(255) NOT NULL,                       -- Senha do usuário
                          nome_completo VARCHAR(255) NOT NULL,               -- Nome completo do usuário
                          data_de_nascimento DATE NOT NULL,                  -- Data de nascimento do usuário
                          tipo VARCHAR(3) CHECK (tipo IN ('JGD', 'ADM', 'CRD', 'NRD')) NOT NULL,  -- Tipo de usuário
                          telefone VARCHAR(15) NOT NULL,                     -- Telefone do usuário
                          menor CHAR(1) CHECK (menor IN ('S', 'N')) NOT NULL, -- Indica se o usuário é menor de idade
                          responsavel VARCHAR(255),                          -- Nome do responsável
                          telefone_responsavel VARCHAR(15),                  -- Telefone do responsável
                          apelido VARCHAR(255),                              -- Apelido
                          email_verified BOOLEAN DEFAULT FALSE               -- Email verificado
);

-- Adiciona índice para melhorar a performance nas buscas por email
CREATE INDEX idx_email ON usuarios(email);