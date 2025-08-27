-- Adiciona coluna apelido na tabela usuarios
ALTER TABLE usuarios ADD COLUMN apelido VARCHAR(255);

-- Atualiza os usuários existentes para que o apelido seja igual ao nome completo, caso não possuam apelido
UPDATE usuarios SET apelido = nome_completo WHERE apelido IS NULL OR apelido = '';

