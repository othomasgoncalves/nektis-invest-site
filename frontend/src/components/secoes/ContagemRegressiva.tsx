"use client";

import { useSyncExternalStore } from "react";
import { formatarPrazo } from "@/lib/formato";

type ContagemRegressivaProps = {
  prazo: string;
  inscricoesAbertas: boolean;
};

type Restante = { dias: number; horas: number; minutos: number; segundos: number };

function restanteDe(alvo: number, agoraMs: number): Restante {
  const segundos = Math.max(0, Math.floor((alvo - agoraMs) / 1000));
  return {
    dias: Math.floor(segundos / 86400),
    horas: Math.floor((segundos % 86400) / 3600),
    minutos: Math.floor((segundos % 3600) / 60),
    segundos: segundos % 60,
  };
}

function assinarRelogio(aoTique: () => void): () => void {
  const id = window.setInterval(aoTique, 1000);
  return () => window.clearInterval(id);
}

const snapshotRelogio = () => Math.floor(Date.now() / 1000);
const snapshotRelogioServidor = () => 0;

const preencher = (valor: number) => String(valor).padStart(2, "0");

export function ContagemRegressiva({
  prazo,
  inscricoesAbertas,
}: ContagemRegressivaProps) {
  const alvo = new Date(prazo).getTime();

  const agoraSegundos = useSyncExternalStore(
    assinarRelogio,
    snapshotRelogio,
    snapshotRelogioServidor,
  );

  if (!inscricoesAbertas || Number.isNaN(alvo)) return null;

  const restante =
    agoraSegundos === 0 ? null : restanteDe(alvo, agoraSegundos * 1000);

  const unidades: Array<[string, string]> = [
    [restante ? preencher(restante.dias) : "--", "Dias"],
    [restante ? preencher(restante.horas) : "--", "Horas"],
    [restante ? preencher(restante.minutos) : "--", "Min"],
    [restante ? preencher(restante.segundos) : "--", "Seg"],
  ];

  return (
    <section style={{ background: "var(--color-ink)", color: "#fff" }}>
      <div
        className="nk-shell flex flex-wrap items-center justify-between"
        style={{ gap: 32, paddingBlock: 34 }}
      >
        <div>
          <p
            style={{
              fontFamily: "var(--font-display)",
              fontWeight: 500,
              fontSize: "clamp(20px, 2.2vw, 26px)",
              letterSpacing: "-0.01em",
            }}
          >
            Inscrições de lançamento até {formatarPrazo(prazo)}
          </p>
          <p style={{ marginTop: 6, fontSize: 14, color: "rgba(255,255,255,.65)" }}>
            Depois disso, o grupo fecha para novas entradas.
          </p>
        </div>

        <div
          className="flex items-start"
          style={{ gap: "clamp(20px, 3vw, 38px)" }}
          role="timer"
          aria-live="off"
        >
          {unidades.map(([valor, rotulo]) => (
            <div key={rotulo} className="text-center">
              <p
                className="tabular-nums"
                style={{
                  fontFamily: "var(--font-display)",
                  fontWeight: 500,
                  fontSize: "clamp(30px, 3.4vw, 42px)",
                  lineHeight: 1,
                  letterSpacing: "-0.02em",
                }}
              >
                {valor}
              </p>
              <p
                style={{
                  marginTop: 8,
                  fontSize: 11,
                  fontWeight: 700,
                  letterSpacing: "0.14em",
                  textTransform: "uppercase",
                  color: "rgba(255,255,255,.55)",
                }}
              >
                {rotulo}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
