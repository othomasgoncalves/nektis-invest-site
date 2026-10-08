# Nektis Invest — invest.nektis.tech

Landing page e fluxo de assinatura da **Nektis Invest**: um grupo fechado no
WhatsApp com a leitura diária do mercado feita por João Pedro.

- **Frontend:** Next.js 16 (App Router) · React 19 · TypeScript strict · Tailwind CSS v4
- **Backend:** Java 25 · Spring Boot 4.1.1 · Spring Data JPA · PostgreSQL 16 · Flyway
- **Pagamentos:** Stripe Checkout (assinatura mensal recorrente) · gateway falso no perfil `dev`
- **E-mail:** Resend · log no perfil `dev`

```
/frontend          Next.js
/backend           Spring Boot (Maven wrapper)
/docs/referencia   design de referência
docker-compose.yml
.env.example
```

---

## Rodando com Docker (caminho recomendado)

Pré-requisitos: Docker.

```bash
cp .env.example .env
docker compose up --build
```

Pronto: <http://localhost:3000>. O Flyway aplica as migrações sozinho e os três
serviços sobem com healthcheck.

| Serviço  | URL                                            |
|----------|------------------------------------------------|
| Frontend | <http://localhost:3000>                        |
| API      | <http://localhost:8080/api/v1>                 |
| Health   | <http://localhost:8080/actuator/health>        |
| Postgres | `localhost:5432`                               |

Com `SPRING_PROFILES_ACTIVE=dev` (o padrão do `.env.example`) **nenhuma chave é
necessária**: o pagamento usa o gateway falso e os e-mails vão para o log do
backend (`docker compose logs -f backend`).

Para ver os links do WhatsApp no card 3, preencha no `.env`:

```dotenv
WHATSAPP_NOTICIAS_URL=https://chat.whatsapp.com/...
WHATSAPP_NETWORKING_URL=https://chat.whatsapp.com/...
```

Se ficarem vazios, o card mostra *“Os links chegam no seu e-mail em breve.”*

---

## Rodando local sem Docker

Pré-requisitos: Node 24, JDK 25 e um PostgreSQL 16.

**Banco** (ou use só o container do Postgres: `docker compose up -d postgres`):

```bash
createdb nektis_invest
```

**Backend**

```bash
cd backend
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

**Frontend**

```bash
cd frontend
npm install
echo 'NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1' > .env.local
npm run dev
```

### Checagens

```bash
cd frontend && npm run lint && npx tsc --noEmit
cd backend  && ./mvnw verify          # exige Docker (Testcontainers)
```

---

## O fluxo de entrada

Os três cards da seção **Sua entrada** são funcionais e o estado sobrevive ao
desvio até o gateway (`sessionStorage` guarda `cadastroId` + `tokenAcesso`).

1. **Cadastro** → `POST /cadastros` (validação com Zod no cliente e Bean Validation no servidor)
2. **Pagamento** → `POST /cadastros/{id}/pagamento` → redireciona para o pagamento do gateway
3. **Acesso liberado** → o gateway devolve o usuário em
   `/obrigado?cadastro={id}&token={t}`, que faz polling em
   `GET /cadastros/{id}/situacao` a cada 3s (máx. 2 min) até o webhook ativar a assinatura

No perfil `dev`, o passo 2 abre uma página de **pagamento simulado** servida pelo
próprio backend; confirmar ali dispara um webhook real pelo mesmo caminho de
código que a Stripe usaria.

---

## API

Base: `/api/v1`. Erros em [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)
(`application/problem+json`). CORS restrito a `URL_FRONTEND`.

| Método | Rota                          | Descrição |
|--------|-------------------------------|-----------|
| GET    | `/oferta`                     | `{ precoCentavos, moeda, prazoInscricao, inscricoesAbertas }` |
| POST   | `/cadastros`                  | `{ nome, email, telefone }` → `201 { cadastroId, tokenAcesso }`; `409` se as inscrições estiverem encerradas |
| POST   | `/cadastros/{id}/pagamento`   | header `X-Token-Acesso` → `{ urlPagamento }` |
| GET    | `/cadastros/{id}/situacao`    | header `X-Token-Acesso` → `{ situacao, links? }` — os links **só** saem com situação `ATIVA` |
| POST   | `/webhooks/pagamentos`        | valida a assinatura do gateway, grava em `evento_pagamento` (idempotente) e atualiza a assinatura |
| GET    | `/actuator/health`            | healthcheck |

Situações da assinatura: `PENDENTE`, `ATIVA`, `INADIMPLENTE`, `CANCELADA`.
Canais de acesso: `NOTICIAS`, `NETWORKING`.

O `tokenAcesso` tem 32 bytes aleatórios e é guardado apenas como hash SHA-256.
Os POSTs públicos têm rate limit por IP.

---

## Stripe (perfil de produção)

1. No painel da Stripe, crie um **Price recorrente mensal em BRL** e copie o `price_...`.
2. Preencha no `.env`:

```dotenv
SPRING_PROFILES_ACTIVE=prod
STRIPE_SECRET_KEY=sk_...
STRIPE_PRICE_ID=price_...
STRIPE_WEBHOOK_SECRET=whsec_...
RESEND_API_KEY=re_...
EMAIL_REMETENTE=Nektis Invest <invest@nektis.tech>
```

3. Para testar os webhooks na sua máquina:

```bash
stripe listen --forward-to localhost:8080/api/v1/webhooks/pagamentos
```

O comando imprime o `whsec_...` que vai em `STRIPE_WEBHOOK_SECRET`.

Eventos tratados: `checkout.session.completed`, `invoice.paid`,
`invoice.payment_failed`, `customer.subscription.updated` e
`customer.subscription.deleted`. Cada entrega é gravada em `evento_pagamento` sob
índice único, então reentregas da Stripe não refazem o trabalho.

O `success_url` aponta para `/obrigado?cadastro={id}&token={t}` e o `cancel_url`
para `/#cadastro`.

---

## Produção

- Rode atrás de HTTPS e ajuste `URL_FRONTEND`, `URL_PUBLICA_API` e
  `NEXT_PUBLIC_API_URL` para os domínios reais.
- **`CONFIAR_PROXY=true` atrás de proxy reverso.** O rate limit ignora o
  `X-Forwarded-For` por padrão, porque o header é forjável e permitiria burlar o
  limite. Com um proxy na frente e `false`, todas as requisições parecem vir do
  IP do proxy e o limite de 10/min passa a valer para o site inteiro. O proxy
  precisa **sobrescrever** o `X-Forwarded-For`, não apenas acrescentar.
- `NEXT_PUBLIC_*` entra no bundle **em tempo de build** — o `docker-compose.yml`
  já passa essas variáveis como build args do frontend.
- Segredos só por variável de ambiente; nada de chave versionada.
- O remetente de `EMAIL_REMETENTE` precisa estar em um domínio já verificado na Resend.

---

## Aviso

O conteúdo da Nektis Invest tem caráter informativo e educacional.
Investimentos envolvem riscos, incluindo perda do capital. Rentabilidade passada
não é garantia de rentabilidade futura.
