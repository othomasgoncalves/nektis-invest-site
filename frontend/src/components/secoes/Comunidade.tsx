const MENSAGENS = [
  {
    tema: "Renda fixa",
    quando: "Hoje",
    resumo: "Juros futuros sobem após ata do banco central.",
    acao: "manter posição pós-fixada, sem alavancagem.",
  },
  {
    tema: "Câmbio",
    quando: "Ontem",
    resumo: "Dólar recua com fluxo estrangeiro positivo.",
    acao: "aguardar antes de novas compras em moeda forte.",
  },
];

const CONVERSA = [
  {
    iniciais: "AM",
    autor: "Membro • Luanda",
    texto: "Alguém já estruturou reserva em moeda forte fora do país?",
  },
  {
    iniciais: "RS",
    autor: "Membro • São Paulo",
    texto: "Sim, fiz no ano passado. Posso partilhar o passo a passo.",
  },
  {
    iniciais: "JP",
    autor: "João Pedro",
    texto: "Ótimo tema. Amanhã trago um resumo sobre isso no canal de notícias.",
  },
];

function BadgeCanal({ tom, children }: { tom: "escuro" | "claro"; children: string }) {
  const escuro = tom === "escuro";
  return (
    <span
      style={{
        display: "inline-block",
        borderRadius: "var(--radius-pill)",
        padding: "7px 14px",
        fontSize: 11,
        fontWeight: 700,
        letterSpacing: "0.14em",
        textTransform: "uppercase",
        background: escuro ? "var(--color-accent)" : "#fff",
        color: escuro ? "var(--color-ink)" : "rgba(62,28,89,.8)",
      }}
    >
      {children}
    </span>
  );
}

export function Comunidade() {
  return (
    <section
      id="comunidade"
      style={{ paddingBlock: "clamp(72px, 9vw, 110px)" }}
    >
      <div className="nk-shell">
        <p className="nk-eyebrow">A comunidade</p>
        <h2 style={{ marginTop: 18, maxWidth: 680 }}>
          Dois canais no WhatsApp, cada um com uma função.
        </h2>

        <div
          className="mt-14 grid"
          style={{
            gridTemplateColumns:
              "repeat(auto-fit, minmax(min(100%, 460px), 1fr))",
            gap: "clamp(20px, 2.4vw, 32px)",
          }}
        >
          <article
            style={{
              background: "var(--color-ink)",
              color: "#fff",
              borderRadius: "var(--radius-card-lg)",
              padding: "clamp(26px, 3vw, 38px)",
            }}
          >
            <BadgeCanal tom="escuro">Canal 01</BadgeCanal>
            <h3
              style={{
                marginTop: 20,
                fontFamily: "var(--font-display)",
                fontWeight: 500,
                fontSize: "clamp(24px, 2.6vw, 30px)",
                letterSpacing: "-0.015em",
              }}
            >
              Notícias diárias
            </h3>
            <p
              style={{
                marginTop: 14,
                fontSize: 16,
                lineHeight: 1.6,
                color: "rgba(255,255,255,.75)",
              }}
            >
              Todos os dias, João Pedro resume o que movimentou o mercado e diz
              qual ação aplicar. Leitura de dois minutos.
            </p>

            <div className="mt-8 flex flex-col gap-4">
              {MENSAGENS.map((mensagem) => (
                <div
                  key={mensagem.tema}
                  style={{
                    background: "#fff",
                    color: "var(--color-ink)",
                    borderRadius: 16,
                    padding: "16px 18px",
                  }}
                >
                  <div className="flex items-baseline justify-between gap-4">
                    <span
                      style={{
                        fontSize: 13,
                        fontWeight: 700,
                        color: "var(--color-accent)",
                      }}
                    >
                      {mensagem.tema}
                    </span>
                    <span
                      style={{ fontSize: 12, color: "rgba(62,28,89,.45)" }}
                    >
                      {mensagem.quando}
                    </span>
                  </div>
                  <p style={{ marginTop: 10, fontSize: 15, lineHeight: 1.5 }}>
                    {mensagem.resumo}
                  </p>
                  <p
                    style={{
                      marginTop: 12,
                      paddingTop: 12,
                      borderTop: "1px solid rgba(62,28,89,.08)",
                      fontSize: 14,
                      color: "rgba(62,28,89,.7)",
                    }}
                  >
                    <strong style={{ color: "var(--color-accent)" }}>
                      Ação:
                    </strong>{" "}
                    {mensagem.acao}
                  </p>
                </div>
              ))}
            </div>

            <p
              style={{
                marginTop: 18,
                fontSize: 12,
                color: "rgba(255,255,255,.5)",
              }}
            >
              Exemplos ilustrativos.
            </p>
          </article>

          <article
            style={{
              background: "var(--color-sand)",
              borderRadius: "var(--radius-card-lg)",
              padding: "clamp(26px, 3vw, 38px)",
            }}
          >
            <BadgeCanal tom="claro">Canal 02</BadgeCanal>
            <h3
              style={{
                marginTop: 20,
                fontFamily: "var(--font-display)",
                fontWeight: 500,
                fontSize: "clamp(24px, 2.6vw, 30px)",
                letterSpacing: "-0.015em",
              }}
            >
              Networking
            </h3>
            <p
              style={{
                marginTop: 14,
                fontSize: 16,
                lineHeight: 1.6,
                color: "rgba(62,28,89,.7)",
              }}
            >
              Um espaço para os membros trocarem experiências, oportunidades e
              contatos com pessoas que pensam o dinheiro a longo prazo.
            </p>

            <div className="mt-8 flex flex-col gap-4">
              {CONVERSA.map((bolha, indice) => (
                <div
                  key={bolha.iniciais}
                  className="flex items-start gap-3"
                  style={{ marginLeft: indice * 14 }}
                >
                  <span
                    aria-hidden="true"
                    className="inline-flex shrink-0 items-center justify-center"
                    style={{
                      width: 36,
                      height: 36,
                      borderRadius: 12,
                      background: "var(--color-lilac)",
                      color: "var(--color-ink)",
                      fontSize: 12,
                      fontWeight: 700,
                    }}
                  >
                    {bolha.iniciais}
                  </span>
                  <div
                    style={{
                      background: "#fff",
                      borderRadius: 16,
                      padding: "12px 16px",
                    }}
                  >
                    <p
                      style={{
                        fontSize: 12,
                        fontWeight: 700,
                        color: "var(--color-accent)",
                      }}
                    >
                      {bolha.autor}
                    </p>
                    <p style={{ marginTop: 5, fontSize: 15, lineHeight: 1.5 }}>
                      {bolha.texto}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </article>
        </div>
      </div>
    </section>
  );
}
