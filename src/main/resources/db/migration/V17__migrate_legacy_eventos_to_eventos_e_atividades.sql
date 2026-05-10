-- Best-effort migration from legacy single-table events to event + activity model
-- Uses safe defaults for free-text legacy schedule fields.

-- 1) Create a new event row for each legacy event
INSERT INTO eventos (id, nome, local, inicio, fim)
SELECT
    el.id,
    COALESCE(NULLIF(el.titulo, ''), 'Evento legado'),
    COALESCE(NULLIF(el.local, ''), 'Local legado'),
    COALESCE(el.data::timestamp, NOW()),
    COALESCE(el.data::timestamp + INTERVAL '1 hour', NOW() + INTERVAL '1 hour')
FROM eventos_legado el
ON CONFLICT (id) DO NOTHING;

-- Keep sequence in sync after explicit ids
SELECT setval('eventos_id_seq', COALESCE((SELECT MAX(id) FROM eventos), 1), true);

-- 2) Create one RPG activity for each migrated event
INSERT INTO atividades (
    evento_id,
    tipo,
    nome,
    descricao,
    inicio,
    fim,
    local_complemento,
    sistema,
    numero_vagas,
    tags,
    narrador_id
)
SELECT
    el.id,
    'RPG_MESA',
    COALESCE(NULLIF(el.titulo, ''), 'Atividade legada'),
    COALESCE(NULLIF(el.descricao, ''), 'Migrado automaticamente do modelo legado.'),
    COALESCE(e.inicio, NOW()),
    COALESCE(e.fim, NOW() + INTERVAL '1 hour'),
    'Migrado do legado',
    el.sistema,
    CASE WHEN el.numero_de_vagas > 0 THEN el.numero_de_vagas ELSE 1 END,
    el.tags,
    CASE
        WHEN TRIM(COALESCE(el.narrador, '')) ~ '^[0-9]+$'
            AND EXISTS (SELECT 1 FROM usuarios u WHERE u.id = CAST(TRIM(el.narrador) AS BIGINT))
        THEN CAST(TRIM(el.narrador) AS BIGINT)
        ELSE NULL
    END
FROM eventos_legado el
JOIN eventos e ON e.id = el.id
WHERE NOT EXISTS (
    SELECT 1 FROM atividades a
    WHERE a.evento_id = el.id
      AND a.tipo = 'RPG_MESA'
);

-- 3) Migrate valid participant ids from legacy CSV players field
INSERT INTO atividade_participantes (atividade_id, usuario_id, created_at)
SELECT
    a.id,
    CAST(TRIM(j.value) AS BIGINT),
    NOW()
FROM eventos_legado el
JOIN atividades a ON a.evento_id = el.id AND a.tipo = 'RPG_MESA'
CROSS JOIN LATERAL regexp_split_to_table(COALESCE(el.jogadores, ''), ',') AS j(value)
WHERE TRIM(j.value) ~ '^[0-9]+$'
  AND EXISTS (SELECT 1 FROM usuarios u WHERE u.id = CAST(TRIM(j.value) AS BIGINT))
ON CONFLICT DO NOTHING;

