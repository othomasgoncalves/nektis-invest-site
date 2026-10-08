const ETAPAS = [
  {
    numero: "01",
    titulo: "Cadastro",
    descricao: "Nome, e-mail e WhatsApp. Leva menos de um minuto.",
  },
  {
    numero: "02",
    titulo: "Pagamento",
    descricao: "Assinatura mensal recorrente de R$ 319,90 no cartão.",
  },
  {
    numero: "03",
    titulo: "Acesso",
    descricao:
      "O link dos canais no WhatsApp é liberado na hora, na tela e por e-mail.",
  },
  {
    numero: "04",
    titulo: "Rotina diária",
    descricao:
      "Todo dia, a notícia resumida e a ação a aplicar, direto do João Pedro.",
  },
];

export function ComoFunciona() {
  return (
    <section
      id="como-funciona"
      style={{ paddingBlock: "clamp(72px, 9vw, 110px)" }}
    >
      <div className="nk-shell">
        <p className="nk-eyebrow">Como funciona</p>
        <h2 style={{ marginTop: 18, maxWidth: 560 }}>
          Da inscrição ao primeiro resumo em poucos minutos.
        </h2>

        <ol
          className="mt-14 grid list-none p-0"
          style={{
            gridTemplateColumns:
              "repeat(auto-fit, minmax(min(100%, 240px), 1fr))",
            borderTop: "1px solid rgba(62,28,89,.16)",
            margin: 0,
          }}
        >
          {ETAPAS.map((etapa, indice) => (
            <li
              key={etapa.numero}
              className="nk-step"
              style={{
                padding: "34px 24px 40px",
                borderLeft:
                  indice === 0 ? "none" : "1px solid rgba(62,28,89,.12)",
                transition: "background-color 0.25s ease",
              }}
            >
              <p className="flex items-center" style={{ gap: 10 }}>
                <span
                  aria-hidden="true"
                  style={{
                    width: 6,
                    height: 6,
                    background: "var(--color-accent)",
                    display: "inline-block",
                  }}
                />
                <span
                  style={{
                    fontFamily: "var(--font-display)",
                    fontWeight: 400,
                    fontSize: 15,
                    color: "var(--color-accent)",
                  }}
                >
                  {etapa.numero}
                </span>
              </p>

              <h3
                style={{
                  marginTop: 22,
                  fontSize: 22,
                  fontWeight: 600,
                  letterSpacing: "-0.015em",
                }}
              >
                {etapa.titulo}
              </h3>
              <p
                style={{
                  marginTop: 14,
                  fontSize: 15,
                  lineHeight: 1.6,
                  color: "rgba(62,28,89,.65)",
                }}
              >
                {etapa.descricao}
              </p>
            </li>
          ))}
        </ol>
      </div>
    </section>
  );
}
