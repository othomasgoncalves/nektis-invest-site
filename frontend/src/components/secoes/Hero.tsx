import Image from "next/image";
import joaoPedro from "@/../public/imagens/joao-pedro.webp";

export function Hero() {
  return (
    <section
      id="topo"
      style={{ background: "var(--color-sand)" }}
      className="overflow-hidden"
    >
      <div
        className="nk-shell grid items-end"
        style={{
          gridTemplateColumns: "repeat(auto-fit, minmax(min(100%, 460px), 1fr))",
          gap: "clamp(40px, 5vw, 72px)",
          paddingTop: "clamp(64px, 9vw, 120px)",
        }}
      >
        <div
          style={{
            animation: "var(--animate-nk-rise)",
            alignSelf: "center",
            paddingBottom: "clamp(64px, 9vw, 120px)",
          }}
        >
          <h1>
            Onde deixar o seu dinheiro{" "}
            <span style={{ color: "var(--color-accent)" }}>
              seguro e rentável.
            </span>
          </h1>

          <p
            style={{
              marginTop: 26,
              maxWidth: 520,
              fontSize: 19,
              lineHeight: 1.55,
              color: "rgba(62,28,89,.75)",
            }}
          >
            Um grupo fechado no WhatsApp com a leitura diária do mercado feita
            por João Pedro: a notícia resumida e a ação a aplicar, sem promessa
            de dinheiro fácil.
          </p>

          <div className="mt-9 flex flex-wrap items-center gap-4">
            <a href="#cadastro" className="nk-btn nk-btn-lg nk-btn-primary">
              Entrar no grupo
            </a>
            <a href="#video" className="nk-btn nk-btn-lg nk-btn-ghost">
              <span
                aria-hidden="true"
                className="inline-flex items-center justify-center rounded-full"
                style={{
                  width: 30,
                  height: 30,
                  background: "var(--color-ink)",
                  color: "#fff",
                }}
              >
                <IconePlay tamanho={11} />
              </span>
              Assistir apresentação
            </a>
          </div>
        </div>

        <div className="relative mx-auto w-full" style={{ maxWidth: 580 }}>
          <div className="nk-hero-figure">
            <div aria-hidden="true" className="nk-hero-arc" />

            <div
              aria-hidden="true"
              className="absolute"
              style={{
                width: 16,
                height: 16,
                background: "var(--color-accent)",
                top: "8%",
                right: "2%",
                zIndex: 1,
              }}
            />

            <div
              className="nk-hero-badge"
              style={{
                background: "#fff",
                borderRadius: 20,
                padding: "18px 22px",
                boxShadow: "0 18px 50px rgba(62,28,89,.10)",
              }}
            >
              <p
                style={{
                  fontFamily: "var(--font-display)",
                  fontWeight: 500,
                  fontSize: 30,
                  lineHeight: 1.1,
                }}
              >
                6 anos
              </p>
              <p
                style={{
                  marginTop: 4,
                  fontSize: 13,
                  color: "rgba(62,28,89,.6)",
                }}
              >
                em fundos de investimento e Forex
              </p>
            </div>

            <Image
              src={joaoPedro}
              alt="João Pedro, responsável pelo conteúdo da Nektis Invest"
              priority
              placeholder="blur"
              sizes="(max-width: 900px) 70vw, 580px"
              className="nk-hero-photo"
            />

            <div
              className="nk-hero-news"
              style={{
                background: "#fff",
                borderRadius: 22,
                padding: 18,
                boxShadow: "0 24px 60px rgba(62,28,89,.14)",
                animation: "var(--animate-nk-float)",
              }}
            >
              <div className="flex items-center gap-3">
                <span
                  aria-hidden="true"
                  className="inline-flex shrink-0 items-center justify-center rounded-full"
                  style={{
                    width: 38,
                    height: 38,
                    background: "var(--color-ink)",
                    color: "#fff",
                    fontSize: 13,
                    fontWeight: 700,
                  }}
                >
                  JP
                </span>
                <div>
                  <p style={{ fontSize: 15, fontWeight: 700 }}>
                    Notícias do dia
                  </p>
                  <p style={{ fontSize: 13, color: "rgba(62,28,89,.55)" }}>
                    Canal • Nektis Invest
                  </p>
                </div>
              </div>

              <div
                style={{
                  marginTop: 14,
                  background: "var(--color-sand)",
                  borderRadius: 14,
                  padding: 14,
                }}
              >
                <p className="nk-eyebrow" style={{ fontSize: 11 }}>
                  Resumo
                </p>
                <p style={{ marginTop: 6, fontSize: 14, lineHeight: 1.5 }}>
                  Juros futuros sobem após ata do banco central.
                </p>
                <p
                  className="nk-eyebrow"
                  style={{ fontSize: 11, marginTop: 14 }}
                >
                  Ação a aplicar
                </p>
                <p style={{ marginTop: 6, fontSize: 14, lineHeight: 1.5 }}>
                  Manter renda fixa pós-fixada. Evitar alavancagem hoje.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

export function IconePlay({ tamanho = 14 }: { tamanho?: number }) {
  return (
    <svg
      width={tamanho}
      height={tamanho}
      viewBox="0 0 12 14"
      fill="currentColor"
      aria-hidden="true"
    >
      <path d="M12 7 0 14V0z" />
    </svg>
  );
}
