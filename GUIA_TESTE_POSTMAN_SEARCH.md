# Guia de Teste - Endpoint de Busca Paginada de Usuários

## Pré-requisitos
1. Ter um usuário ADM ou CRD cadastrado no sistema
2. Fazer login e obter o token JWT

## Passo 1: Obter Token JWT (Login)
```
POST http://localhost:8080/api/public/auth/login
Content-Type: application/json

Body:
{
  "email": "admin@email.com",
  "password": "senha123"
}

Resposta:
{
  "statusCode": 200,
  "statusMessage": "OK",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBlbWFpbC5jb20iLCJub21lQ29tcGxldG8iOiJBZG1pbmlzdHJhZG9yIiwidGlwbyI6IkFETSIsImlkIjoxLCJpYXQiOjE3NjI5OTk1NjMsImV4cCI6MTc2MzA4NTk2M30.xyz..."
}
```

## Passo 2: Configurar Postman para o Endpoint de Busca

### Configuração Básica
1. **Método:** GET
2. **URL:** `http://localhost:8080/api/user/search`
3. **Headers:**
   - Clique na aba "Headers"
   - Adicione: `Authorization` com valor `Bearer <seu_token_aqui>`
   - **IMPORTANTE:** Cole o token JWT completo obtido no login após "Bearer "

### Aba Authorization (Alternativa)
1. Selecione "Type" = "Bearer Token"
2. Cole o token JWT no campo "Token"

## Passo 3: Exemplos de Testes

### Teste 1: Busca Simples (Primeira Página)
```
GET http://localhost:8080/api/user/search

Params: (nenhum - usa valores padrão)
Headers: Authorization: Bearer <token>

Resultado Esperado:
- Página 0
- 3 usuários por página (default ajustado)
- Ordenado por nomeCompleto (asc)
```

### Teste 2: Buscar Apenas Jogadores
```
GET http://localhost:8080/api/user/search?tipos=JGD

Params:
  - tipos: JGD

Headers: Authorization: Bearer <token>

Resultado Esperado:
- Apenas usuários do tipo JGD
```

### Teste 3: Buscar Menores de Idade
```
GET http://localhost:8080/api/user/search?menor=S

Params:
  - menor: S

Headers: Authorization: Bearer <token>

Resultado Esperado:
- Apenas usuários com campo menor = "S"
```

### Teste 4: Múltiplos Filtros
```
GET http://localhost:8080/api/user/search?tipos=JGD,NRD&menor=N&page=0&size=5

Params:
  - tipos: JGD,NRD
  - menor: N
  - page: 0
  - size: 5

Headers: Authorization: Bearer <token>

Resultado Esperado:
- Usuários tipo JGD ou NRD
- Que sejam maiores de idade (menor=N)
- 5 por página
```

### Teste 5: Ordenação por Email Decrescente
```
GET http://localhost:8080/api/user/search?sort=email&dir=desc

Params:
  - sort: email
  - dir: desc

Headers: Authorization: Bearer <token>

Resultado Esperado:
- Ordenado por email de Z para A
```

### Teste 6: Paginação - Segunda Página
```
GET http://localhost:8080/api/user/search?page=1&size=5

Params:
  - page: 1
  - size: 5

Headers: Authorization: Bearer <token>

Resultado Esperado:
- Itens 6 a 10 (segunda página)
- Campo "number": 1
- Campo "first": false
```

### Teste 7: Ordenação por Apelido
```
GET http://localhost:8080/api/user/search?sort=apelido&dir=asc

Params:
  - sort: apelido
  - dir: asc

Headers: Authorization: Bearer <token>

Resultado Esperado:
- Ordenado por apelido A-Z
```

## Teste de Segurança

### Teste 8: Sem Token (Deve Falhar)
```
GET http://localhost:8080/api/user/search

Headers: (sem Authorization)

Resultado Esperado:
- Status 401 Unauthorized
- Mensagem: "Token inválido ou usuário não encontrado"
```

### Teste 9: Token de Usuário Não-Admin (Deve Falhar)
```
# Faça login com usuário tipo JGD
POST http://localhost:8080/api/public/auth/login
Body: {"email": "jogador1@email.com", "password": "senha"}

# Use o token retornado no endpoint de busca
GET http://localhost:8080/api/user/search
Headers: Authorization: Bearer <token_de_jogador>

Resultado Esperado:
- Status 403 Forbidden
- Mensagem: "Acesso negado: Apenas administradores ou coordenadores podem acessar este recurso"
```

### Teste 10: Token Expirado (Deve Falhar)
```
GET http://localhost:8080/api/user/search
Headers: Authorization: Bearer <token_expirado>

Resultado Esperado:
- Status 401 Unauthorized
```

## Campos Importantes na Resposta

### Resposta de Sucesso
```json
{
  "statusCode": 200,
  "statusMessage": "OK",
  "data": {
    "content": [
      {
        "id": 1,
        "email": "admin@email.com",
        "nomeCompleto": "Administrador Sistema",
        "dataDeNascimento": "1990-01-01",
        "tipo": "ADM",
        "telefone": "11999999999",
        "menor": "N",
        "responsavel": null,
        "telefoneResponsavel": null,
        "apelido": "Admin"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 3
    },
    "totalElements": 15,
    "totalPages": 5,
    "size": 3,
    "number": 0,
    "first": true,
    "last": false,
    "empty": false
  }
}
```

### Campos Úteis para o Frontend
- **content**: Array de usuários da página atual
- **totalElements**: Total de registros (para mostrar "Mostrando X de Y")
- **totalPages**: Total de páginas (para navegação)
- **number**: Página atual (começa em 0)
- **first**: Se é a primeira página (desabilitar botão "anterior")
- **last**: Se é a última página (desabilitar botão "próximo")
- **empty**: Se não há resultados (mostrar mensagem "Nenhum usuário encontrado")

## Validações Importantes

### ✅ O que o endpoint valida:
1. **Token JWT presente:** Verifica se o header Authorization existe
2. **Token válido:** Verifica se o JWT é válido e não expirado
3. **Usuário existe:** Verifica se o email do token existe no banco
4. **Permissão ADM/CRD:** Verifica se o tipo é ADM ou CRD

### ⚠️ Tipos de usuário aceitos:
- **ADM** (Administrador) ✅
- **CRD** (Coordenador) ✅
- **JGD** (Jogador) ❌
- **NRD** (Narrador) ❌

## Dicas de Debug

### Verificar Token no JWT.io
1. Copie o token JWT
2. Cole em https://jwt.io
3. Verifique o campo "sub" (deve ser o email)
4. Verifique o campo "tipo" (deve ser ADM ou CRD)
5. Verifique o campo "exp" (não pode estar expirado)

### Se receber 401:
- Verifique se o token está completo
- Verifique se tem "Bearer " antes do token
- Verifique se o token não expirou
- Verifique se o usuário existe no banco

### Se receber 403:
- Verifique o tipo do usuário no banco
- Confirme que é ADM ou CRD
- Verifique se o email do token corresponde ao usuário correto

### Se receber 500:
- Verifique os logs da aplicação
- Pode ser erro de conexão com banco
- Pode ser erro no formato dos dados

