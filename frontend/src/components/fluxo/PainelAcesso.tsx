import type { LinkAcesso } from "@/lib/api";

const ROTULO_CANAL: Record<LinkAcesso["canal"], string> = {
  NOTICIAS: "Entrar em Notícias diárias",
  NETWORKING: "Entrar em Networking",
};

export function PainelAcesso({ links }: { links: LinkAcesso[] }) {
  return (
    <>
      <span
        aria-hidden="true"
        className="inline-flex items-center justify-center rounded-full"
        style={{ width: 56, height: 56, background: "var(--color-ink)", color: "#fff" }}
      >
        <svg width="24" height="19" viewBox="0 0 24 19" fill="none" aria-hidden="true">
          <path
            d="M2 9.5 9 16.5 22 2.5"
            stroke="currentColor"
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </span>

      <p
        style={{
          marginTop: 22,
          fontFamily: "var(--font-display)",
          fontWeight: 500,
          fontSize: 24,
          letterSpacing: "-0.02em",
        }}
      >
        Bem-vindo ao grupo.
      </p>

      {links.length > 0 ? (
        <>
          <p
            style={{
              marginTop: 10,
              fontSize: 15,
              lineHeight: 1.55,
              color: "rgba(62,28,89,.72)",
            }}
          >
            Use os links abaixo para entrar nos dois canais. Eles também foram
            enviados para o seu e-mail.
          </p>

          <div className="mt-6 flex flex-col" style={{ gap: 12 }}>
            {links.map((link) => (
              <a
                key={link.canal}
                href={link.url}
                target="_blank"
                rel="noopener noreferrer"
                className="nk-access-link flex items-center justify-between"
                style={{
                  background: "#fff",
                  borderRadius: 14,
                  padding: "16px 18px",
                  fontSize: 15,
                  transition: "background-color .2s ease, color .2s ease",
                }}
              >
                {ROTULO_CANAL[link.canal]}
                <span aria-hidden="true">→</span>
              </a>
            ))}
          </div>
        </>
      ) : (
        <p
          style={{
            marginTop: 10,
            fontSize: 15,
            lineHeight: 1.55,
            color: "rgba(62,28,89,.72)",
          }}
        >
          Os links chegam no seu e-mail em breve.
        </p>
      )}
    </>
  );
}
