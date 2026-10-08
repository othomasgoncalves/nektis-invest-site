import type { CSSProperties, ReactNode } from "react";

type CardEtapaProps = {
  titulo: string;
  indice: 1 | 2 | 3;
  ativo: boolean;
  tom?: "branco" | "lilas";
  children: ReactNode;
  style?: CSSProperties;
};

export function CardEtapa({
  titulo,
  indice,
  ativo,
  tom = "branco",
  children,
  style,
}: CardEtapaProps) {
  return (
    <section
      aria-current={ativo ? "step" : undefined}
      style={{
        background: tom === "lilas" ? "var(--color-lilac)" : "#fff",
        border:
          tom === "lilas"
            ? "1px solid transparent"
            : `1px solid rgba(62,28,89,${ativo ? ".28" : ".12"})`,
        borderRadius: "var(--radius-card-lg)",
        padding: "clamp(22px, 2.4vw, 30px)",
        opacity: ativo ? 1 : 0.55,
        transition: "opacity .3s ease, border-color .3s ease",
        ...style,
      }}
    >
      <header className="flex items-baseline justify-between gap-4">
        <h3
          style={{
            fontFamily: "var(--font-display)",
            fontWeight: 500,
            fontSize: 19,
            letterSpacing: "-0.015em",
          }}
        >
          {indice}. {titulo}
        </h3>
        <span
          style={{
            fontSize: 13,
            color: ativo ? "var(--color-accent)" : "rgba(62,28,89,.45)",
          }}
        >
          {indice}/3
        </span>
      </header>

      <div style={{ marginTop: 22 }}>{children}</div>
    </section>
  );
}
