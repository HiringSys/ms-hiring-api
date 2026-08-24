# ms-hiring-api

API REST do HiringSys, desenvolvida com Java 21, Spring Boot, Spring MVC e
PostgreSQL. O projeto utiliza banco de dados persistente no lugar da `ArrayList`
proposta no enunciado, conforme alinhamento com o professor.

## Funcionários

O CRUD completo está disponível em `/funcionarios`:

```http
POST   /funcionarios
GET    /funcionarios
GET    /funcionarios/{id}
PUT    /funcionarios/{id}
PATCH  /funcionarios/{id}
DELETE /funcionarios/{id}
```

A listagem aceita os filtros opcionais `nome`, `cargo` e `status`, combinados
na mesma requisição. Os indicadores estão em `GET /funcionarios/indicadores`.

O cadastro exige nome, e-mail e ao menos um cargo. Os campos de funcionário
incluem telefone, salário, cidade, departamento, status e experiência.

Como o Hibernate está configurado com `ddl-auto=validate`, bancos criados antes
da inclusão de departamento precisam ser alinhados uma única vez:

```sql
ALTER TABLE funcionario
    ADD COLUMN IF NOT EXISTS departamento VARCHAR(100);
```

## Front-end

O cliente Vue deve configurar `VITE_API_BASE_URL` com a URL desta API. O CORS
aceita a origem definida em `FRONTEND_ORIGIN`.

## Recuperação de senha

Configure `SENDGRID_API_KEY`, `SENDGRID_FROM_EMAIL` e `SENDGRID_FROM_NAME` no
arquivo `.env`. O remetente precisa estar verificado no SendGrid.

Solicite uma nova senha com:

```http
POST /auth/password-recovery
Content-Type: application/json

{"email":"rh@hiringsys.local"}
```

A rota sempre responde com `202 Accepted` e uma mensagem genérica quando a
requisição é processada, evitando revelar se o e-mail possui uma conta.

## Logout

Encerre a sessão atual enviando o token recebido no login:

```http
POST /auth/logout
Authorization: Bearer <accessToken>
```

A API responde com `204 No Content` e passa a rejeitar esse token.
