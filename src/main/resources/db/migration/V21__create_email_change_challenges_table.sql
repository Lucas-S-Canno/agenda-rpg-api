-- Criar tabela de desafios para troca de email autenticada
CREATE TABLE IF NOT EXISTS email_change_challenges (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    code VARCHAR(20) NOT NULL,
    verification_token VARCHAR(200) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    validated_at TIMESTAMP,
    used_at TIMESTAMP,
    attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_change_challenge_user FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ix_email_change_challenges_user_id ON email_change_challenges(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_email_change_challenges_verification_token ON email_change_challenges(verification_token);

