-- Criar tabela de tokens de verificação de email (caso não exista)
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(200) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    consumed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resend_count INTEGER DEFAULT 0,
    resend_available_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Criar índices (caso não existam)
CREATE UNIQUE INDEX IF NOT EXISTS ux_email_verif_token ON email_verification_tokens(token);
CREATE INDEX IF NOT EXISTS ix_email_verif_user ON email_verification_tokens(user_id);
