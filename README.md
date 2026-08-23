# ms-hiring-api

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
