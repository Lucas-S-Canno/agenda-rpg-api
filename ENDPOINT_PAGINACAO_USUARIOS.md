# Endpoint de Paginação e Filtros de Usuários

## Endpoint
```
GET /api/user/search
```

## Descrição
Este endpoint permite buscar usuários com paginação, filtros e ordenação customizável.

## ⚠️ Autenticação e Autorização
- **Requer autenticação:** Sim
- **Header obrigatório:** `Authorization: Bearer <token>`
- **Permissões:** Apenas usuários com tipo **ADM** (Administrador) ou **CRD** (Coordenador) podem acessar este endpoint
- **Validação:** O email do usuário é extraído do token JWT (claim "sub") e validado no banco de dados

## Parâmetros Query String

| Parâmetro | Tipo | Obrigatório | Padrão | Descrição |
|-----------|------|-------------|---------|-----------|
| `page` | int | Não | 0 | Número da página (começa em 0) |
| `size` | int | Não | 10 | Quantidade de itens por página |
| `tipos` | String | Não | - | Tipos de usuário separados por vírgula (ex: "ADM,CRD,JGD,NRD") |
| `menor` | String | Não | - | Filtro se é menor de idade: "S" ou "N" |
| `sort` | String | Não | nomeCompleto | Campo para ordenação: "nomeCompleto", "email" ou "apelido" |
| `dir` | String | Não | asc | Direção da ordenação: "asc" (crescente) ou "desc" (decrescente) |

## Exemplos de Uso

**Nota:** Todos os exemplos requerem o header `Authorization: Bearer <seu_token_jwt>`

### 1. Buscar todos os usuários (página 0, 10 por página, ordenado por nome completo)
```
GET http://localhost:8080/api/user/search
Headers: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 2. Buscar administradores e coordenadores
```
GET http://localhost:8080/api/user/search?tipos=ADM,CRD
Headers: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 3. Buscar apenas usuários maiores de idade
```
GET http://localhost:8080/api/user/search?menor=N
```

### 4. Buscar jogadores menores de idade
```
GET http://localhost:8080/api/user/search?tipos=JGD&menor=S
```

### 5. Buscar página 2 com 20 itens, ordenado por email decrescente
```
GET http://localhost:8080/api/user/search?page=2&size=20&sort=email&dir=desc
```

### 6. Buscar narradores ordenados por apelido
```
GET http://localhost:8080/api/user/search?tipos=NRD&sort=apelido&dir=asc
```

### 7. Buscar todos os tipos exceto ADM, ordenado por nome decrescente
```
GET http://localhost:8080/api/user/search?tipos=JGD,NRD,CRD&sort=nomeCompleto&dir=desc
```

## Resposta

### Estrutura de Resposta de Sucesso (200 OK)
```json
{
  "statusCode": 200,
  "statusMessage": "OK",
  "data": {
    "content": [
      {
        "id": 1,
        "email": "usuario@example.com",
        "nomeCompleto": "João Silva",
        "dataDeNascimento": "1990-01-01",
        "tipo": "JGD",
        "telefone": "11999999999",
        "menor": "N",
        "responsavel": null,
        "telefoneResponsavel": null,
        "apelido": "JoaoS"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 50,
    "totalPages": 5,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

### Campos de Paginação
- `content`: Array com os usuários da página atual
- `totalElements`: Total de usuários encontrados (considerando filtros)
- `totalPages`: Total de páginas disponíveis
- `size`: Quantidade de itens por página
- `number`: Número da página atual (começa em 0)
- `first`: true se é a primeira página
- `last`: true se é a última página
- `empty`: true se não há resultados

### Respostas de Erro

#### 401 Unauthorized - Token inválido ou usuário não encontrado
```json
{
  "statusCode": 401,
  "statusMessage": "Token inválido ou usuário não encontrado",
  "data": null
}
```

#### 403 Forbidden - Usuário não tem permissão
```json
{
  "statusCode": 403,
  "statusMessage": "Acesso negado: Apenas administradores ou coordenadores podem acessar este recurso",
  "data": null
}
```

#### 500 Internal Server Error
```json
{
  "statusCode": 500,
  "statusMessage": "Internal Server Error",
  "data": null
}
```

## Testes no Postman

### Teste 1: Busca simples
```
GET http://localhost:8080/user/search
```

### Teste 2: Busca com todos os filtros
```
GET http://localhost:8080/user/search?page=0&size=5&tipos=JGD,NRD&menor=N&sort=apelido&dir=asc
```

### Teste 3: Segunda página
```
GET http://localhost:8080/user/search?page=1&size=10
```

## Notas
- Se `tipos` for omitido ou vazio, retorna todos os tipos
- Se `menor` for omitido, retorna usuários maiores e menores
- Valores inválidos para `menor` (diferente de "S" ou "N") são ignorados
- O campo `sort` aceita apenas: "nomeCompleto", "email" ou "apelido" (caso contrário, usa "nomeCompleto")
- A direção `dir` aceita apenas: "asc" ou "desc" (caso contrário, usa "asc")
- Valores negativos para `page` são ajustados para 0
- Valores menores que 1 para `size` são ajustados para 1

