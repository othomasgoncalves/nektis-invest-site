CREATE TABLE oferta (
    id              BIGSERIAL PRIMARY KEY,
    preco_centavos  INTEGER     NOT NULL CHECK (preco_centavos > 0),
    moeda           VARCHAR(3)  NOT NULL DEFAULT 'BRL',
    prazo_inscricao TIMESTAMPTZ NOT NULL,
    ativo           BOOLEAN     NOT NULL DEFAULT TRUE
);

INSERT INTO oferta (preco_centavos, moeda, prazo_inscricao, ativo)
VALUES (31990, 'BRL', TIMESTAMPTZ '2026-10-26T23:59:59-03:00', TRUE);

CREATE TABLE assinante (
    id                UUID         PRIMARY KEY,
    nome              VARCHAR(120) NOT NULL,
    email             VARCHAR(180) NOT NULL UNIQUE,
    telefone          VARCHAR(20)  NOT NULL,
    hash_token_acesso VARCHAR(64)  NOT NULL,
    criado_em         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assinante_email ON assinante (email);

CREATE TABLE assinatura (
    id                    UUID        PRIMARY KEY,
    assinante_id          UUID        NOT NULL REFERENCES assinante (id) ON DELETE CASCADE,
    gateway               VARCHAR(40) NOT NULL,
    gateway_cliente_id    VARCHAR(255),
    gateway_assinatura_id VARCHAR(255),
    situacao              VARCHAR(20) NOT NULL
        CHECK (situacao IN ('PENDENTE', 'ATIVA', 'INADIMPLENTE', 'CANCELADA')),
    fim_periodo_atual     TIMESTAMPTZ,
    criado_em             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assinatura_assinante ON assinatura (assinante_id);
CREATE INDEX idx_assinatura_gateway_ass ON assinatura (gateway_assinatura_id);
CREATE INDEX idx_assinatura_gateway_cli ON assinatura (gateway_cliente_id);

CREATE TABLE evento_pagamento (
    id                BIGSERIAL    PRIMARY KEY,
    gateway_evento_id VARCHAR(255) NOT NULL UNIQUE,
    tipo              VARCHAR(120) NOT NULL,
    payload           JSONB        NOT NULL,
    recebido_em       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE link_acesso (
    id    BIGSERIAL    PRIMARY KEY,
    canal VARCHAR(20)  NOT NULL UNIQUE CHECK (canal IN ('NOTICIAS', 'NETWORKING')),
    url   VARCHAR(500) NOT NULL,
    ativo BOOLEAN      NOT NULL DEFAULT TRUE
);
