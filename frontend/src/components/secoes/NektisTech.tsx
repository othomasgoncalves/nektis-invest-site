export function NektisTech() {
  return (
    <section style={{ paddingBottom: "clamp(64px, 8vw, 100px)" }}>
      <div className="nk-shell">
        <a
          href="https://nektis.tech/"
          target="_blank"
          rel="noopener noreferrer"
          className="flex flex-wrap items-center justify-between gap-6 transition-colors"
          style={{
            background: "var(--color-sand)",
            borderRadius: "var(--radius-card-lg)",
            padding: "clamp(26px, 3vw, 34px) clamp(26px, 3vw, 40px)",
          }}
        >
          <div className="flex items-start" style={{ gap: 16 }}>
            <span
              aria-hidden="true"
              style={{
                width: 10,
                height: 10,
                background: "var(--color-accent)",
                display: "inline-block",
                marginTop: 7,
              }}
            />
            <div>
              <p className="nk-eyebrow">Uma iniciativa Nektis Tech</p>
              <p style={{ marginTop: 8, fontSize: 17 }}>
                A mesma equipe que desenvolve soluções sob medida para empresas.
                Tecnologia com propósito.
              </p>
            </div>
          </div>

          <span style={{ fontSize: 16, fontWeight: 700 }}>
            nektis.tech <span aria-hidden="true">→</span>
          </span>
        </a>
      </div>
    </section>
  );
}
