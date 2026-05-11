# Documentacao detalhada da API - Agenda RPG Admin

Este documento descreve o estado atual da API (`Spring Boot 3.4`, `Java 17`) para facilitar manutencao, integracao com frontend e testes.

## 1) Visao geral

- Base URL local: `http://localhost:8080/api`
- Context path: `server.servlet.context-path=/api`
- Formato padrao de resposta:

```json
{
  "statusCode": 200,
  "statusMessage": "OK",
  "data": {}
}
```

- Envelope usado: `ResponseDTO<T>`
- Excecao conhecida: `GET /api/tags/{id}` retorna `TagDTO` puro

## 2) Autenticacao e autorizacao

## 2.1 JWT

- Login gera token JWT em `POST /api/login`
- Claims relevantes:
    - `sub`: email
    - `tipo`: perfil do usuario
    - `id`: id do usuario

Header para rotas protegidas:

```http
Authorization: Bearer <token>
```

Se faltar token ou for invalido, o filtro JWT pode retornar `401` em texto simples.

## 2.2 Perfis de usuario

- `ADM` (admin)
- `CRD` (coordenador)
- `NRD` (narrador)
- `JGD` (jogador)

## 2.3 Matriz de acesso (resumo)

### Publico

- `/api/login`
- `/api/health`
- `/api/public/**`
- `GET /api/events`
- `GET /api/events/{id}`
- `GET /api/events/{eventId}/activities`

### Autenticado (qualquer perfil)

- `GET /api/activities/{id}`
- `POST|DELETE /api/activities/{id}/register`
- `/api/user-app/**`
- `/api/tags/**`

### Restrito por perfil

- Eventos (`POST|PUT|DELETE /api/events/**`): `ADM` ou `CRD`
- Atividades (`POST /api/events/{eventId}/activities`, `PUT|DELETE /api/activities/{id}`): `ADM`, `CRD` ou `NRD`
- Gestao de usuarios (`/api/user/**`): `ADM` ou `CRD`

## 3) Endpoints por dominio

## 3.1 Saude

### `GET /api/health`
- Acesso: publico
- Retorno: `ResponseDTO<String>` com `data = "OK"`

## 3.2 Autenticacao

### `POST /api/login`
- Acesso: publico
- Body:

```json
{
  "email": "admin@email.com",
  "password": "Senha@123"
}
```

- Sucesso: token JWT em `data`
- Erros comuns:
    - `401` credenciais invalidas
    - `403` email nao verificado

## 3.3 Usuario publico e recuperacao de senha

Base: `/api/public/user`

### `GET /test`
- Acesso: publico

### `POST /register`
- Acesso: publico
- DTO: `UserDTO`
- Campos importantes:
    - obrigatorios: `email`, `password`, `nomeCompleto`, `dataDeNascimento`, `tipo`, `telefone`, `apelido`, `menor`
    - se `menor = "true"`, exige `responsavel` e `telefoneResponsavel`

Exemplo:

```json
{
  "email": "joao@example.com",
  "password": "password123",
  "nomeCompleto": "Joao Silva",
  "dataDeNascimento": "2001-02-01",
  "tipo": "JGD",
  "telefone": "(11)98765-4321",
  "menor": "false",
  "apelido": "joao"
}
```

### `POST /forgot-password`
- Body:

```json
{ "email": "joao@example.com" }
```

### `POST /validate-reset-code`
- Body:

```json
{ "email": "joao@example.com", "code": "123456" }
```

### `POST /reset-password`
- Body:

```json
{
  "email": "joao@example.com",
  "newPassword": "NovaSenha@123",
  "resetToken": "token_recebido_na_validacao"
}
```

## 3.4 Verificacao de email

Base: `/api/public/email-validation`

- `GET /verify-email?token=...`
- `POST /resend-verification` com body `{ "email": "..." }`

Pode retornar `429` em limite/cooldown de reenvio.

## 3.5 Eventos publicos legados

Base: `/api/public/event`

- `GET /`
- `GET /{id}`

Observacao: continuam existentes para compatibilidade, mas o frontend novo pode priorizar `/api/events`.

## 3.6 Eventos (modelo novo)

Base: `/api/events`

- `GET /` (publico)
- `GET /{id}` (publico)
- `POST /` (`ADM`/`CRD`)
- `PUT /{id}` (`ADM`/`CRD`)
- `DELETE /{id}` (`ADM`/`CRD`)

DTO atual: `EventDTO`

```json
{
  "id": 1,
  "nome": "RPG Day Dezembro",
  "local": "Centro de Convencoes",
  "inicio": "2025-12-01T09:00:00",
  "fim": "2025-12-02T18:00:00",
  "atividades": []
}
```

Regras:
- `nome`, `local`, `inicio`, `fim` obrigatorios
- `fim` deve ser maior que `inicio`

## 3.7 Atividades (modelo novo)

### Leitura por evento

- `GET /api/events/{eventId}/activities` (publico)

### CRUD de atividade

- `POST /api/events/{eventId}/activities` (`ADM`/`CRD`/`NRD`)
- `GET /api/activities/{id}` (autenticado)
- `PUT /api/activities/{id}` (`ADM`/`CRD`/`NRD`)
- `DELETE /api/activities/{id}` (`ADM`/`CRD`/`NRD`)

### Inscricao/desinscricao

- `POST /api/activities/{id}/register` (autenticado)
- `DELETE /api/activities/{id}/register` (autenticado)

### Consultas do usuario

- `GET /api/user-app/activities/my-registrations` (autenticado)
- `GET /api/user-app/activities/my-creations` (autenticado)

DTO atual: `ActivityDTO`

```json
{
  "id": 10,
  "eventoId": 1,
  "tipo": "RPG_MESA",
  "nome": "Mesa D&D",
  "descricao": "Sessao para iniciantes",
  "inicio": "2025-12-01T14:00:00",
  "fim": "2025-12-01T17:00:00",
  "localComplemento": "Sala 1",
  "sistema": "D&D 5e",
  "numeroVagas": 6,
  "tags": ["fantasia", "aventura"],
  "narradorId": 1,
  "tema": null,
  "palestranteId": null,
  "participantes": []
}
```

Regras de negocio:
- Comuns: `tipo`, `nome`, `descricao`, `inicio`, `fim`, `localComplemento`
- `fim > inicio`
- Janela da atividade deve estar dentro da janela do evento
- `RPG_MESA`: exige `sistema`, `numeroVagas > 0`, `narradorId` valido, `tags`
- `WORKSHOP`: exige `tema`, `palestranteId` valido
- Inscricao bloqueia duplicidade
- `RPG_MESA` respeita lotacao

## 3.8 Perfil do usuario autenticado

Base: `/api/user-app/user`

- `GET /me`
- `GET /narrator-name/{narratorId}`
- `PUT /update-profile/{userId}`
- `PUT /change-password`

Regra relevante de update:
- usuario so altera o proprio perfil
- `nomeCompleto`, `dataDeNascimento` e `tipo` nao podem ser alterados por esse fluxo

Body de `change-password`:

```json
{
  "senhaAtual": "Senha@123",
  "novaSenha": "NovaSenha@123",
  "confirmacaoNovaSenha": "NovaSenha@123"
}
```

## 3.9 Gestao de usuarios (admin app)

Base: `/api/user`

- `POST /validate-admin`
- `GET /search?page=0&size=10&tipos=ADM,CRD&menor=N&sort=nomeCompleto&dir=asc`
- `GET /`
- `POST /`
- `PUT /{id}`
- `DELETE /{id}`

Acesso esperado: `ADM` ou `CRD`.

## 3.10 Tags

Base: `/api/tags`

- `GET /`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{id}`

Observacoes:
- Atualmente exige autenticacao (qualquer perfil)
- `PUT` espera `id` no body

## 3.11 Endpoints legados/deprecated

Base: `/api/user-app/event`

- `GET /{id}`
- `GET /my-events` (`410`)
- `GET /registered-events` (`410`)
- `POST /` (`410`)
- `PUT /{id}` (`410`)
- `PATCH /{id}/register` (`410`)
- `PATCH /{id}/unregister` (`410`)

Recomendacao: nao usar em novos fluxos de frontend.

## 4) Codigos de erro mais comuns

- `400`: validacao de negocio (ex.: datas invalidas)
- `401`: token ausente/invalido ou usuario nao autenticado
- `403`: sem permissao de role
- `404`: recurso nao encontrado
- `409`: conflito (ex.: inscricao duplicada/lotacao)
- `410`: endpoint legado descontinuado
- `429`: limite/cooldown de reenvio de email
- `500`: erro interno

## 5) Setup local e testes

## 5.1 Dependencias

```powershell
docker compose up -d
```

## 5.2 Subir API

```powershell
./gradlew.bat bootRun
```

## 5.3 Rodar testes

```powershell
./gradlew.bat test
```

## 5.4 Testar com Postman

- Importar `Postman_Collection.json` (raiz)
- Definir `base_url` para `http://localhost:8080/api`
- Executar fluxo sugerido:
    1. login
    2. criar evento (ADM/CRD)
    3. criar atividade (ADM/CRD/NRD)
    4. listar eventos/atividades sem token
    5. inscrever usuario em atividade

## 6) Referencias de codigo

- `src/main/java/com/agendarpgadmin/api/configs/SecurityConfig.java`
- `src/main/java/com/agendarpgadmin/api/filters/JwtAuthenticationFilter.java`
- `src/main/java/com/agendarpgadmin/api/controllers/publics/*.java`
- `src/main/java/com/agendarpgadmin/api/controllers/adminApp/*.java`
- `src/main/java/com/agendarpgadmin/api/controllers/UsersApp/*.java`
- `src/main/java/com/agendarpgadmin/api/dtos/EventDTO.java`
- `src/main/java/com/agendarpgadmin/api/dtos/ActivityDTO.java`
- `src/main/java/com/agendarpgadmin/api/services/AdminApp/EventService.java`
- `src/main/java/com/agendarpgadmin/api/services/AdminApp/ActivityService.java`
- `src/main/java/com/agendarpgadmin/api/services/UsersApp/UserAppActivityService.java`
- `Postman_Collection.json`

