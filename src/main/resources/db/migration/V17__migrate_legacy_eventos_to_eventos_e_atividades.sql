-- Best-effort migration from legacy single-table events to event + activity model
-- Refactored to support UUID primary keys

-- 1) Create new events using random UUIDs and mapping them from legacy IDs
WITH mapping AS (
    SELECT 
        el.id as legacy_id,
        gen_random_uuid() as new_uuid
    FROM eventos_legado el
    WHERE NOT EXISTS (SELECT 1 FROM eventos e WHERE e.nome = el.titulo) -- Evita duplicatas se rodar de novo
),
inserted_events AS (
    INSERT INTO eventos (id, nome, local, inicio, fim)
    SELECT
        m.new_uuid,
        COALESCE(NULLIF(el.titulo, ''), 'Evento legado'),
        COALESCE(NULLIF(el.local, ''), 'Local legado'),
        COALESCE(el.data::timestamp, NOW()),
        COALESCE(el.data::timestamp + INTERVAL '1 hour', NOW() + INTERVAL '1 hour')
    FROM eventos_legado el
    JOIN mapping m ON m.legacy_id = el.id
    RETURNING id
)
-- 2) Create one RPG activity for each migrated event
INSERT INTO atividades (
    id,
    evento_id,
    tipo,
    nome,
    descricao,
    inicio,
    fim,
    local_complemento,
    sistema,
    numero_vagas,
    tags
)
SELECT
    gen_random_uuid(),
    m.new_uuid,
    'RPG_MESA',
    COALESCE(NULLIF(el.titulo, ''), 'Atividade legada'),
    COALESCE(NULLIF(el.descricao, ''), 'Migrado automaticamente do modelo legado.'),
    COALESCE(el.data::timestamp, NOW()),
    COALESCE(el.data::timestamp + INTERVAL '1 hour', NOW() + INTERVAL '1 hour'),
    'Migrado do legado',
    el.sistema,
    CASE WHEN el.numero_de_vagas > 0 THEN el.numero_de_vagas ELSE 1 END,
    el.tags
FROM eventos_legado el
JOIN mapping m ON m.legacy_id = el.id;

-- Note: Participant migration was removed because user IDs are now UUIDs 
-- and there is no direct mapping from legacy BIGINT IDs to new UUIDs in this script.
-- If needed, a mapping table for users would be required.
