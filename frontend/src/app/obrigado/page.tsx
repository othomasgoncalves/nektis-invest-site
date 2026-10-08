import type { Metadata } from "next";
import { Footer } from "@/components/secoes/Footer";
import { Header } from "@/components/secoes/Header";
import { ObrigadoClient } from "./ObrigadoClient";

export const metadata: Metadata = {
  title: "Pagamento confirmado — Nektis Invest",
  robots: { index: false, follow: false },
};

function primeiro(valor: string | string[] | undefined): string {
  if (Array.isArray(valor)) return valor[0] ?? "";
  return valor ?? "";
}

export default async function ObrigadoPage({
  searchParams,
}: {
  searchParams: Promise<Record<string, string | string[] | undefined>>;
}) {
  const params = await searchParams;

  return (
    <>
      <Header />
      <main style={{ background: "var(--color-sand)" }}>
        <div
          className="nk-shell"
          style={{ paddingBlock: "clamp(64px, 9vw, 120px)" }}
        >
          <div className="text-center">
            <p className="nk-eyebrow">Sua entrada</p>
            <h1 style={{ marginTop: 18, fontSize: "clamp(32px, 4.4vw, 52px)" }}>
              Obrigado. Falta só confirmar.
            </h1>
          </div>

          <div style={{ marginTop: 48 }}>
            <ObrigadoClient
              cadastroId={primeiro(params.cadastro)}
              tokenAcesso={primeiro(params.token)}
            />
          </div>
        </div>
      </main>
      <Footer />
    </>
  );
}
