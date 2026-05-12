-- Track which user created each event
ALTER TABLE eventos
    ADD COLUMN IF NOT EXISTS creator_user_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_eventos_creator_user'
    ) THEN
        ALTER TABLE eventos
            ADD CONSTRAINT fk_eventos_creator_user
            FOREIGN KEY (creator_user_id)
            REFERENCES usuarios(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_eventos_creator_user_id ON eventos(creator_user_id);

