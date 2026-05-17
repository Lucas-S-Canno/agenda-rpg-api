-- Normalize legacy identifier columns to UUID in a deterministic way.
-- This migration is idempotent for columns already typed as UUID.

CREATE OR REPLACE FUNCTION legacy_text_to_uuid(p_input text)
RETURNS uuid
LANGUAGE plpgsql
AS $$
DECLARE
    v_input text;
    v_hash text;
BEGIN
    IF p_input IS NULL THEN
        RETURN NULL;
    END IF;

    v_input := btrim(p_input);

    -- Keep valid UUID values untouched.
    IF v_input ~* '^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$' THEN
        RETURN v_input::uuid;
    END IF;

    -- Deterministic fallback mapping for legacy numeric/text identifiers.
    v_hash := md5(v_input);
    RETURN (
        substr(v_hash, 1, 8) || '-' ||
        substr(v_hash, 9, 4) || '-' ||
        '4' || substr(v_hash, 14, 3) || '-' ||
        'a' || substr(v_hash, 18, 3) || '-' ||
        substr(v_hash, 21, 12)
    )::uuid;
END;
$$;

CREATE OR REPLACE PROCEDURE normalize_uuid_column(p_table text, p_column text)
LANGUAGE plpgsql
AS $$
DECLARE
    v_data_type text;
BEGIN
    SELECT c.data_type
      INTO v_data_type
      FROM information_schema.columns c
     WHERE c.table_schema = 'public'
       AND c.table_name = p_table
       AND c.column_name = p_column;

    IF v_data_type IS NULL OR v_data_type = 'uuid' THEN
        RETURN;
    END IF;

    EXECUTE format('ALTER TABLE %I ALTER COLUMN %I DROP DEFAULT', p_table, p_column);
    EXECUTE format(
        'ALTER TABLE %I ALTER COLUMN %I TYPE uuid USING legacy_text_to_uuid(%I::text)',
        p_table,
        p_column,
        p_column
    );
END;
$$;

-- Drop existing FKs first to avoid type mismatch during conversion.
DO $$
DECLARE
    r record;
BEGIN
    FOR r IN
        SELECT DISTINCT tc.table_name, tc.constraint_name
          FROM information_schema.table_constraints tc
          LEFT JOIN information_schema.referential_constraints rc
            ON rc.constraint_schema = tc.constraint_schema
           AND rc.constraint_name = tc.constraint_name
          LEFT JOIN information_schema.constraint_table_usage ctu
            ON ctu.constraint_schema = rc.unique_constraint_schema
           AND ctu.constraint_name = rc.unique_constraint_name
         WHERE tc.table_schema = 'public'
           AND tc.constraint_type = 'FOREIGN KEY'
           AND (
               tc.table_name IN (
                   'usuarios',
                   'eventos',
                   'atividades',
                   'atividade_participantes',
                   'email_verification_tokens',
                   'refresh_tokens',
                   'password_reset_codes',
                   'tags'
               )
               OR ctu.table_name IN (
                   'usuarios',
                   'eventos',
                   'atividades',
                   'atividade_participantes',
                   'email_verification_tokens',
                   'refresh_tokens',
                   'password_reset_codes',
                   'tags'
               )
           )
    LOOP
        EXECUTE format('ALTER TABLE %I DROP CONSTRAINT IF EXISTS %I', r.table_name, r.constraint_name);
    END LOOP;
END;
$$;

-- Normalize PK and FK-related columns to UUID.
CALL normalize_uuid_column('usuarios', 'id');
CALL normalize_uuid_column('eventos', 'id');
CALL normalize_uuid_column('eventos', 'creator_user_id');
CALL normalize_uuid_column('atividades', 'id');
CALL normalize_uuid_column('atividades', 'evento_id');
CALL normalize_uuid_column('atividades', 'narrador_id');
CALL normalize_uuid_column('atividades', 'palestrante_id');
CALL normalize_uuid_column('atividade_participantes', 'id');
CALL normalize_uuid_column('atividade_participantes', 'atividade_id');
CALL normalize_uuid_column('atividade_participantes', 'usuario_id');
CALL normalize_uuid_column('email_verification_tokens', 'id');
CALL normalize_uuid_column('email_verification_tokens', 'user_id');
CALL normalize_uuid_column('refresh_tokens', 'id');
CALL normalize_uuid_column('refresh_tokens', 'user_id');
CALL normalize_uuid_column('password_reset_codes', 'id');
CALL normalize_uuid_column('tags', 'id');

-- Recreate expected constraints with stable names.
ALTER TABLE IF EXISTS atividades DROP CONSTRAINT IF EXISTS fk_atividades_evento;
ALTER TABLE IF EXISTS atividades
    ADD CONSTRAINT fk_atividades_evento
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS atividade_participantes DROP CONSTRAINT IF EXISTS fk_atividade_participantes_atividade;
ALTER TABLE IF EXISTS atividade_participantes
    ADD CONSTRAINT fk_atividade_participantes_atividade
    FOREIGN KEY (atividade_id) REFERENCES atividades(id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS atividade_participantes DROP CONSTRAINT IF EXISTS uk_atividade_participante;
ALTER TABLE IF EXISTS atividade_participantes
    ADD CONSTRAINT uk_atividade_participante UNIQUE (atividade_id, usuario_id);

ALTER TABLE IF EXISTS email_verification_tokens DROP CONSTRAINT IF EXISTS fk_email_verif_user;
ALTER TABLE IF EXISTS email_verification_tokens
    ADD CONSTRAINT fk_email_verif_user
    FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS refresh_tokens DROP CONSTRAINT IF EXISTS fk_refresh_token_user;
ALTER TABLE IF EXISTS refresh_tokens
    ADD CONSTRAINT fk_refresh_token_user
    FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS eventos DROP CONSTRAINT IF EXISTS fk_eventos_creator_user;
ALTER TABLE IF EXISTS eventos
    ADD CONSTRAINT fk_eventos_creator_user
    FOREIGN KEY (creator_user_id) REFERENCES usuarios(id);

-- Cleanup helpers created for this migration.
DROP PROCEDURE IF EXISTS normalize_uuid_column(text, text);
DROP FUNCTION IF EXISTS legacy_text_to_uuid(text);


