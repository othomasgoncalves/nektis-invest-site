import { Logo } from "@/components/ui/Logo";

export function Footer() {
  return (
    <footer style={{ background: "var(--color-ink)", color: "#fff" }}>
      <div
        className="nk-shell"
        style={{ paddingBlock: "clamp(44px, 5vw, 60px)" }}
      >
        <div className="flex flex-wrap items-start justify-between gap-6">
          <div>
            <Logo tom="branco" tamanho={24} />
            <p
              style={{
                marginTop: 12,
                fontSize: 15,
                color: "rgba(255,255,255,.75)",
              }}
            >
              invest.nektis.tech
            </p>
          </div>
          <p style={{ fontSize: 14, color: "rgba(255,255,255,.7)" }}>
            © 2026 Nektis. Todos os direitos reservados.
          </p>
        </div>

        <p
          style={{
            marginTop: 34,
            maxWidth: 820,
            fontSize: 12.5,
            lineHeight: 1.6,
            color: "rgba(255,255,255,.55)",
          }}
        >
          O conteúdo da Nektis Invest tem caráter informativo e educacional.
          Investimentos envolvem riscos, incluindo perda do capital.
          Rentabilidade passada não é garantia de rentabilidade futura.
        </p>
      </div>
    </footer>
  );
}
