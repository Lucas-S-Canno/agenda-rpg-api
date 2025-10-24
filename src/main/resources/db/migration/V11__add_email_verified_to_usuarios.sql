-- Adicionar campo email_verified na tabela usuarios (caso não exista)
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT false;

-- Marcar usuários existentes como verificados (política para usuários legados)
UPDATE usuarios SET email_verified = true WHERE email_verified IS NULL OR email_verified = false;
