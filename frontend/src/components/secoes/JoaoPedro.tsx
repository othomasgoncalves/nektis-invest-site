import Image from "next/image";
import joaoPedro from "@/../public/imagens/joao-pedro.webp";

const METRICAS = [
  { valor: "6", rotulo: "anos de mercado" },
  { valor: "Fundos", rotulo: "de investimento" },
  { valor: "Forex", rotulo: "câmbio internacional" },
];

export function JoaoPedro() {
  return (
    <section id="joao" style={{ paddingBlock: "clamp(72px, 9vw, 110px)" }}>
      <div
        className="nk-shell grid items-center"
        style={{
          gridTemplateColumns: "repeat(auto-fit, minmax(min(100%, 420px), 1fr))",
          gap: "clamp(32px, 5vw, 72px)",
        }}
      >
        <div
          className="relative overflow-hidden"
          style={{
            background: "var(--color-lilac)",
            borderRadius: "var(--radius-card-lg)",
            aspectRatio: "4 / 5",
          }}
        >
          <span
            aria-hidden="true"
            className="absolute"
            style={{
              width: 12,
              height: 12,
              background: "var(--color-accent)",
              top: 26,
              left: 26,
              zIndex: 1,
            }}
          />
          <Image
            src={joaoPedro}
            alt="Retrato de João Pedro"
            sizes="(max-width: 900px) 92vw, 560px"
            className="absolute inset-0 h-full w-full object-contain object-bottom"
          />
        </div>

        <div>
          <p className="nk-eyebrow">Quem conduz</p>
          <h2 style={{ marginTop: 18 }}>João Pedro</h2>

          <p
            style={{
              marginTop: 24,
              fontSize: 17,
              lineHeight: 1.6,
              color: "rgba(62,28,89,.75)",
            }}
          >
            Há 6 anos trabalha com fundos de investimento e Forex. É ele quem
            acompanha o mercado e escreve, todos os dias, o conteúdo do grupo.
          </p>
          <p
            style={{
              marginTop: 18,
              fontSize: 17,
              lineHeight: 1.6,
              color: "rgba(62,28,89,.75)",
            }}
          >
            A proposta é simples: traduzir o que acontece no mercado em decisões
            claras, com critério e visão de longo prazo.
          </p>

          <dl
            className="grid"
            style={{
              marginTop: 40,
              paddingTop: 30,
              borderTop: "1px solid rgba(62,28,89,.14)",
              gridTemplateColumns:
                "repeat(auto-fit, minmax(min(100%, 150px), 1fr))",
              gap: 24,
            }}
          >
            {METRICAS.map((metrica) => (
              <div key={metrica.rotulo}>
                <dt
                  style={{
                    fontFamily: "var(--font-display)",
                    fontWeight: 600,
                    fontSize: 26,
                    letterSpacing: "-0.02em",
                  }}
                >
                  {metrica.valor}
                </dt>
                <dd
                  style={{
                    margin: 0,
                    marginTop: 6,
                    fontSize: 14,
                    color: "rgba(62,28,89,.6)",
                  }}
                >
                  {metrica.rotulo}
                </dd>
              </div>
            ))}
          </dl>
        </div>
      </div>
    </section>
  );
}
