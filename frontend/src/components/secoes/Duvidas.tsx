"use client";

import { useId, useState } from "react";

const PERGUNTAS = [
  {
    pergunta: "O grupo promete retorno garantido?",
    resposta:
      "Não. O conteúdo é informativo e educacional. Investimentos envolvem riscos e nenhuma estratégia garante rentabilidade.",
  },
  {
    pergunta: "Como recebo o link do WhatsApp?",
    resposta:
      "Assim que o pagamento é confirmado, os links dos dois canais aparecem na tela e também são enviados para o seu e-mail.",
  },
  {
    pergunta: "Como funciona a cobrança?",
    resposta:
      "É uma assinatura mensal recorrente no cartão. Você pode cancelar quando quiser e o acesso vale até o fim do período pago.",
  },
  {
    pergunta: "Até quando posso entrar?",
    resposta: "As inscrições de lançamento vão até 26 de outubro.",
  },
];

export function Duvidas() {
  const [aberta, setAberta] = useState(0);
  const idBase = useId();

  return (
    <section style={{ paddingBlock: "clamp(72px, 9vw, 110px)" }}>
      <div
        className="nk-shell"
        style={{ maxWidth: "var(--container-faq)" }}
      >
        <h2 className="text-center">Dúvidas frequentes</h2>

        <div style={{ marginTop: 44, borderTop: "1px solid rgba(62,28,89,.12)" }}>
          {PERGUNTAS.map((item, indice) => {
            const expandida = aberta === indice;
            const idPainel = `${idBase}-painel-${indice}`;
            const idBotao = `${idBase}-botao-${indice}`;
            return (
              <div
                key={item.pergunta}
                style={{ borderBottom: "1px solid rgba(62,28,89,.12)" }}
              >
                <h3 style={{ margin: 0 }}>
                  <button
                    type="button"
                    id={idBotao}
                    aria-expanded={expandida}
                    aria-controls={idPainel}
                    onClick={() => setAberta(expandida ? -1 : indice)}
                    className="flex w-full cursor-pointer items-center justify-between gap-6 bg-transparent text-left"
                    style={{
                      border: 0,
                      padding: "26px 0",
                      fontFamily: "var(--font-sans)",
                      fontSize: 17,
                      fontWeight: 500,
                    }}
                  >
                    {item.pergunta}
                    <span
                      aria-hidden="true"
                      style={{
                        fontSize: 20,
                        lineHeight: 1,
                        color: "var(--color-accent)",
                      }}
                    >
                      {expandida ? "−" : "+"}
                    </span>
                  </button>
                </h3>
                <div
                  id={idPainel}
                  role="region"
                  aria-labelledby={idBotao}
                  hidden={!expandida}
                >
                  <p
                    style={{
                      paddingBottom: 26,
                      maxWidth: 760,
                      fontSize: 15,
                      lineHeight: 1.6,
                      color: "rgba(62,28,89,.7)",
                    }}
                  >
                    {item.resposta}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
