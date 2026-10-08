type LogoProps = {
  tom?: "tinta" | "branco";
  tamanho?: number | string;
};

export function Logo({ tom = "tinta", tamanho = 20 }: LogoProps) {
  const cor = tom === "branco" ? "#ffffff" : "var(--color-ink)";
  return (
    <span
      className="inline-flex items-center"
      style={{
        fontFamily: "var(--font-display)",
        fontSize: tamanho,
        lineHeight: 1,
        color: cor,
        gap: "0.4em",
      }}
    >
      <span style={{ fontWeight: 600, letterSpacing: "-0.02em" }}>nektis</span>
      <span
        aria-hidden="true"
        style={{
          width: 6,
          height: 6,
          background: "var(--color-accent)",
          display: "inline-block",
        }}
      />
      <span style={{ fontWeight: 300, letterSpacing: "-0.01em" }}>invest</span>
    </span>
  );
}
