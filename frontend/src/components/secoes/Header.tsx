import { Logo } from "@/components/ui/Logo";

const MENU = [
  { href: "#como-funciona", rotulo: "Como funciona" },
  { href: "#joao", rotulo: "João Pedro" },
  { href: "#comunidade", rotulo: "Comunidade" },
  { href: "#assinatura", rotulo: "Assinatura" },
];

export function Header() {
  return (
    <header
      className="sticky top-0 z-50"
      style={{
        background: "rgba(255,255,255,.82)",
        backdropFilter: "blur(14px)",
        WebkitBackdropFilter: "blur(14px)",
        borderBottom: "1px solid rgba(62,28,89,.08)",
      }}
    >
      <div
        className="nk-shell flex items-center justify-between gap-6"
        style={{ height: "clamp(72px, 6vw, 96px)" }}
      >
        <a href="#topo" aria-label="Nektis Invest — início">
          <Logo tamanho="clamp(17px, 4vw, 21px)" />
        </a>

        <nav aria-label="Seções" className="hidden lg:block">
          <ul className="flex items-center" style={{ gap: 34 }}>
            {MENU.map((item) => (
              <li key={item.href}>
                <a
                  href={item.href}
                  className="transition-colors hover:text-[var(--color-accent)]"
                  style={{ fontSize: 15, color: "rgba(62,28,89,.82)" }}
                >
                  {item.rotulo}
                </a>
              </li>
            ))}
          </ul>
        </nav>

        <a href="#assinatura" className="nk-btn nk-btn-header nk-btn-primary shrink-0">
          Garantir minha vaga
        </a>
      </div>
    </header>
  );
}
