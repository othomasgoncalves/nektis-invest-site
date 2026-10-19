"use client";

import { useId, useState } from "react";

const PERGUNTAS = [
  {
    pergunta: "A Nektis Invest garante rentabilidade?",
    resposta:
      "Não. Investimentos envolvem riscos, oscilações e possibilidade de perdas. Nenhum resultado futuro pode ser garantido.",
  },
  {
    pergunta: "Posso investir apenas com os conteúdos do site?",
    resposta:
      "Eles são material de estudo e não substituem a avaliação da sua situação financeira, dos seus objetivos e da sua tolerância a risco.",
  },
  {
    pergunta: "A Nektis Invest identifica meu perfil de investidor?",
    resposta:
      "Sim. No cadastro, você poderá responder a um questionário sobre objetivos financeiros, experiência com investimentos, horizonte de tempo e tolerância a riscos. As respostas ajudam a compreender o seu perfil de investidor.",
  },
  {
    pergunta: "Como recebo o link do WhatsApp?",
    resposta:
      "Assim que o pagamento é confirmado, os links aparecem na tela e vão também para o seu e-mail.",
  },
  {
    pergunta: "Como funciona a assinatura?",
    resposta:
      "Cobrança mensal recorrente no cartão, com acesso aos materiais da oferta vigente. Cancele quando quiser: o acesso vale até o fim do período pago.",
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
