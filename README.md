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

## Stripe (perfil padrão)

Stripe e Resend são o padrão: eles valem sempre que `SPRING_PROFILES_ACTIVE`
estiver **sem valor**. Não existe um perfil chamado `prod` — o único perfil é o
`dev`, que troca os dois por um gateway falso e e-mail no log.

1. No painel da Stripe, crie um **Price recorrente mensal em BRL** e copie o `price_...`.
2. Preencha no `.env`:

```dotenv
SPRING_PROFILES_ACTIVE=
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

### Como o pacote é montado

Em produção **não existe processo Node**. O frontend é exportado como arquivos
estáticos (`output: "export"` → `frontend/out/`) e copiado para
`backend/src/main/resources/static/`, de onde o Spring Boot o serve. Um único
processo atende tudo, na mesma origem:

```
invest.nektis.tech
  └─ Spring Boot (porta 80 na Square Cloud)
       ├─ /            → index.html
       ├─ /obrigado    → obrigado.html  (forward do PaginasEstaticasController)
       └─ /api/v1/**   → API
```

Mesma origem tem três consequências:

- **Não há CORS.** `NEXT_PUBLIC_API_URL` é o caminho relativo `/api/v1`, fixado
  no build pelo workflow.
- **Os headers de segurança saem do backend.** O `async headers()` do
  next.config.ts só funciona com servidor Next; quem os envia é o
  `HeadersSegurancaFilter`.
- O CSP precisa de `script-src 'unsafe-inline'`: o HTML exportado pelo Next
  carrega os dados de hidratação em `<script>` inline e, sem servidor, não há
  nonce por requisição.

O pacote é gerado pelo workflow `.github/workflows/squarecloud.yml`, que publica
o artefato `nektis-invest-squarecloud` (`nektis-invest.jar` + `squarecloud.app`)
na aba **Actions**. É esse `.zip` que sobe no painel da Square Cloud.

A home é estática, então o preço e o prazo vêm do `OFERTA_PADRAO`
(`frontend/src/lib/oferta.ts`) no primeiro frame e são substituídos pela resposta
de `GET /api/v1/oferta` na hidratação. Mantenha o fallback em dia com a linha
seed de `oferta` na `V1__init.sql`.

### Banco na Neon

- Use o endpoint **direto**, não o `-pooler`: o pool é o Hikari, do lado da
  aplicação, e o pooler da Neon é PgBouncer em modo transação, que atrita com os
  prepared statements do driver JDBC.
- Mantenha `?sslmode=require` na URL.
- O compute da Neon **suspende quando fica ocioso** e derruba as conexões. O pool
  já está ajustado para isso (`minimum-idle: 0`, `max-lifetime: 280s`,
  `connection-timeout: 30s`), então a primeira requisição depois de uma pausa
  pode levar alguns segundos enquanto o compute acorda.
- A string de conexão **nunca** chega ao navegador: só o backend lê
  `SPRING_DATASOURCE_*`, e nenhuma variável `NEXT_PUBLIC_*` toca no banco.
  Diferente do Supabase, a Neon não expõe uma API HTTP sobre as tabelas nem uma
  chave pública de cliente — não há superfície equivalente a RLS para errar.
- O Flyway cria o schema e insere a oferta inicial na primeira subida.

### Variáveis

- **`URL_FRONTEND` é o domínio que o visitante usa, sem barra final** — não o
  subdomínio interno da hospedagem. Ele é a origem liberada no CORS *e* a base do
  `success_url` da Stripe. Apontado para o lugar errado, o cadastro falha com
  `403 Invalid CORS request` e quem paga é devolvido para o domínio errado.
  `URL_PUBLICA_API` recebe o mesmo valor.
- **`CONFIAR_PROXY=true` na Square Cloud.** O flag diz que há um proxy de
  confiança sobrescrevendo os `X-Forwarded-*`, e governa duas coisas:
  - o rate limit passa a tirar o IP do cliente do `X-Forwarded-For`. Com `false`
    atrás de proxy, todas as requisições parecem vir do IP do proxy e o limite de
    10/min passa a valer para o site inteiro.
  - o `ForwardedHeaderFilter` reconstrói o esquema e o host públicos. Sem isso o
    Tomcat enxerga `http://` atrás do TLS do proxy, e o `CorsUtils.isCorsRequest`
    — que compara esquema, host e porta com o `Origin` — passa a tratar o site
    falando com a própria API como requisição entre origens.
- `NEXT_PUBLIC_*` entra no bundle **em tempo de build**: mudar `NEXT_PUBLIC_VIDEO_URL`
  no painel da Square não tem efeito. Defina a variável de repositório no GitHub
  e refaça o build.
- Sem `RESEND_API_KEY` a aplicação **não sobe** no perfil padrão.
- Sem `STRIPE_WEBHOOK_SECRET` todo webhook é **rejeitado com 400** e nenhuma
  assinatura é ativada — o cliente paga e fica preso em "confirmando".
- Segredos só por variável de ambiente; nada de chave versionada.
- O remetente de `EMAIL_REMETENTE` precisa estar em um domínio já verificado na Resend.

---

## Aviso

O conteúdo da Nektis Invest tem caráter informativo e educacional.
Investimentos envolvem riscos, incluindo perda do capital. Rentabilidade passada
não é garantia de rentabilidade futura.
