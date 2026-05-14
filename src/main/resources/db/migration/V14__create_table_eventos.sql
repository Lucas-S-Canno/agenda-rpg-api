-- New events table: event is now the day/container for activities
CREATE TABLE IF NOT EXISTS eventos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    local VARCHAR(255) NOT NULL,
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    creator_user_id UUID
);
