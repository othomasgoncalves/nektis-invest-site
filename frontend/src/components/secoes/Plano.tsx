import type { Oferta } from "@/lib/api";
import { formatarPrazo, formatarValor } from "@/lib/formato";

const INCLUIDO = [
  "Canal de notícias diárias com resumo e ação a aplicar",
  "Canal de networking com os membros",
  "Conteúdo produzido por João Pedro",
  "Vídeo de apresentação e fundamentos",
  "Acesso imediato após o pagamento",
];

function IconeCheck() {
  return (
    <span
      aria-hidden="true"
      className="inline-flex shrink-0 items-center justify-center rounded-full"
      style={{
        width: 22,
        height: 22,
        background: "var(--color-ink)",
        color: "#fff",
        marginTop: 2,
      }}
    >
      <svg width="11" height="9" viewBox="0 0 11 9" fill="none" aria-hidden="true">
        <path
          d="M1 4.6 3.9 7.5 10 1.5"
          stroke="currentColor"
          strokeWidth="1.8"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
    </span>
  );
}

export function Plano({ oferta }: { oferta: Oferta }) {
  return (
    <section
      id="assinatura"
      style={{
        background: "var(--color-sand)",
        paddingBlock: "clamp(72px, 9vw, 120px)",
      }}
    >
      <div className="nk-shell">
        <div className="text-center">
          <p className="nk-eyebrow">Assinatura</p>
          <h2 style={{ marginTop: 18, maxWidth: 620, marginInline: "auto" }}>
            Um plano. Acesso completo aos dois canais.
          </h2>
        </div>

        <div
          className="mx-auto mt-14 grid items-start"
          style={{
            maxWidth: "var(--container-plan)",
            gridTemplateColumns:
              "repeat(auto-fit, minmax(min(100%, 380px), 1fr))",
            gap: "clamp(28px, 4vw, 56px)",
          }}
        >
          <div
            style={{
              background: "#fff",
              border: "2px solid var(--color-ink)",
              borderRadius: "var(--radius-card-lg)",
              padding: "clamp(26px, 3vw, 36px)",
            }}
          >
            <div className="flex items-center justify-between gap-4">
              <p style={{ fontSize: 17, fontWeight: 700 }}>Nektis Invest</p>
              <span
                style={{
                  borderRadius: "var(--radius-pill)",
                  padding: "6px 14px",
                  background: "var(--color-lilac)",
                  fontSize: 12,
                  fontWeight: 700,
                }}
              >
                Mensal
              </span>
            </div>

            <p
              className="flex items-baseline"
              style={{ marginTop: 26, gap: 6 }}
            >
              <span style={{ fontSize: 20, color: "rgba(62,28,89,.7)" }}>
                R$
              </span>
              <span
                style={{
                  fontFamily: "var(--font-display)",
                  fontWeight: 500,
                  fontSize: "clamp(48px, 6vw, 70px)",
                  lineHeight: 1,
                  letterSpacing: "-0.03em",
                }}
              >
                {formatarValor(oferta.precoCentavos)}
              </span>
              <span style={{ fontSize: 16, color: "rgba(62,28,89,.6)" }}>
                /mês
              </span>
            </p>
            <p
              style={{
                marginTop: 12,
                fontSize: 14,
                color: "rgba(62,28,89,.6)",
              }}
            >
              Cobrança recorrente no cartão.
            </p>

            <a
              href="#cadastro"
              className="nk-btn nk-btn-lg nk-btn-primary nk-btn-block"
              style={{ marginTop: 28 }}
            >
              Assinar e entrar no grupo
            </a>

            {oferta.inscricoesAbertas && (
              <p
                className="text-center"
                style={{
                  marginTop: 18,
                  fontSize: 13,
                  color: "rgba(62,28,89,.55)",
                }}
              >
                Inscrições de lançamento até{" "}
                {formatarPrazo(oferta.prazoInscricao)}
              </p>
            )}
          </div>

          <div style={{ paddingTop: 8 }}>
            <h3 style={{ fontSize: 19, fontWeight: 700 }}>O que está incluído</h3>
            <ul className="mt-6 flex list-none flex-col p-0" style={{ gap: 16 }}>
              {INCLUIDO.map((item) => (
                <li key={item} className="flex items-start" style={{ gap: 12 }}>
                  <IconeCheck />
                  <span style={{ fontSize: 16, lineHeight: 1.5 }}>{item}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </section>
  );
}
