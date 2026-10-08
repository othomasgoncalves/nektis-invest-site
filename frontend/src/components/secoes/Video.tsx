"use client";

import Image from "next/image";
import { useCallback, useEffect, useRef, useState } from "react";
import joaoPedro from "@/../public/imagens/joao-pedro.webp";
import { IconePlay } from "./Hero";

const URL_VIDEO = process.env.NEXT_PUBLIC_VIDEO_URL ?? "";

export function Video() {
  const [aberto, setAberto] = useState(false);
  const dialogoRef = useRef<HTMLDivElement>(null);
  const fecharRef = useRef<HTMLButtonElement>(null);
  const temVideo = URL_VIDEO.trim().length > 0;

  const fechar = useCallback(() => setAberto(false), []);

  useEffect(() => {
    if (!aberto) return;
    const aoTeclar = (evento: KeyboardEvent) => {
      if (evento.key === "Escape") fechar();
    };
    document.addEventListener("keydown", aoTeclar);
    const overflowAnterior = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    fecharRef.current?.focus();
    return () => {
      document.removeEventListener("keydown", aoTeclar);
      document.body.style.overflow = overflowAnterior;
    };
  }, [aberto, fechar]);

  return (
    <section id="video" style={{ paddingBlock: "clamp(72px, 9vw, 120px)" }}>
      <div className="nk-shell">
        <div
          className="grid items-end"
          style={{
            gridTemplateColumns:
              "repeat(auto-fit, minmax(min(100%, 460px), 1fr))",
            gap: "clamp(24px, 4vw, 56px)",
          }}
        >
          <div>
            <p className="nk-eyebrow">Apresentação</p>
            <h2 style={{ marginTop: 18 }}>
              Dinheiro fácil não existe. Estratégia, sim.
            </h2>
          </div>
          <p
            style={{
              fontSize: 17,
              lineHeight: 1.6,
              color: "rgba(62,28,89,.7)",
              maxWidth: 460,
            }}
          >
            João Pedro explica o que é o grupo, como funciona a rotina diária e
            por que desconfiar de quem promete retorno garantido.
          </p>
        </div>

        <div
          className="relative mt-12 overflow-hidden"
          style={{
            background: "var(--color-sand)",
            borderRadius: "var(--radius-card-lg)",
            aspectRatio: "16 / 8",
          }}
        >
          <div
            aria-hidden="true"
            className="absolute hidden md:block"
            style={{
              left: "50%",
              right: "6%",
              top: "12%",
              bottom: 0,
              background: "var(--color-lilac)",
              borderRadius: "50% 50% 0 0",
            }}
          />
          <Image
            src={joaoPedro}
            alt=""
            aria-hidden="true"
            sizes="(max-width: 768px) 0px, 30vw"
            className="absolute bottom-0 hidden h-[92%] w-auto object-contain object-bottom md:block"
            style={{ right: "12%" }}
          />

          <div
            className="absolute flex items-center"
            style={{
              left: "clamp(20px, 3.4vw, 52px)",
              bottom: "clamp(24px, 3.6vw, 56px)",
              gap: 24,
            }}
          >
            <button
              type="button"
              onClick={() => temVideo && setAberto(true)}
              disabled={!temVideo}
              aria-label={
                temVideo
                  ? "Assistir: O que é a Nektis Invest"
                  : "Vídeo em breve"
              }
              className="inline-flex shrink-0 items-center justify-center rounded-full transition-transform disabled:cursor-not-allowed disabled:opacity-60"
              style={{
                width: "clamp(56px, 5.2vw, 76px)",
                height: "clamp(56px, 5.2vw, 76px)",
                background: "var(--color-ink)",
                color: "#fff",
              }}
              onMouseEnter={(e) => {
                if (temVideo) e.currentTarget.style.transform = "scale(1.06)";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = "scale(1)";
              }}
            >
              <IconePlay tamanho={22} />
            </button>

            <div>
              <p
                style={{
                  fontFamily: "var(--font-display)",
                  fontWeight: 500,
                  fontSize: "clamp(19px, 2.1vw, 26px)",
                  letterSpacing: "-0.015em",
                }}
              >
                {temVideo ? "O que é a Nektis Invest" : "Vídeo em breve"}
              </p>
              <p
                style={{
                  marginTop: 4,
                  fontSize: 15,
                  color: "rgba(62,28,89,.6)",
                }}
              >
                com João Pedro
              </p>
            </div>
          </div>
        </div>
      </div>

      {aberto && temVideo && (
        <div
          role="dialog"
          aria-modal="true"
          aria-label="Vídeo de apresentação da Nektis Invest"
          ref={dialogoRef}
          onMouseDown={(evento) => {
            if (evento.target === dialogoRef.current) fechar();
          }}
          className="fixed inset-0 z-[100] flex items-center justify-center p-5"
          style={{ background: "rgba(62,28,89,.72)" }}
        >
          <div className="w-full" style={{ maxWidth: 1040 }}>
            <div className="mb-3 flex justify-end">
              <button
                ref={fecharRef}
                type="button"
                onClick={fechar}
                className="nk-btn"
                style={{ background: "#fff", color: "var(--color-ink)" }}
              >
                Fechar
              </button>
            </div>
            <div
              className="overflow-hidden"
              style={{ borderRadius: 18, aspectRatio: "16 / 9", background: "#000" }}
            >
              <iframe
                src={URL_VIDEO}
                title="O que é a Nektis Invest"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
                className="h-full w-full border-0"
              />
            </div>
          </div>
        </div>
      )}
    </section>
  );
}
