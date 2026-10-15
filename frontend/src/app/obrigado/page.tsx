import type { Metadata } from "next";
import { Suspense } from "react";
import { Footer } from "@/components/secoes/Footer";
import { Header } from "@/components/secoes/Header";
import { ObrigadoClient } from "./ObrigadoClient";

export const metadata: Metadata = {
  title: "Pagamento confirmado — Nektis Invest",
  robots: { index: false, follow: false },
};

export default function ObrigadoPage() {
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
            {/* A página é estática: o cadastro e o token vêm da query string,
                lida no cliente. O Suspense é o que o useSearchParams exige
                para a casca da página poder ser pré-renderizada no build. */}
            <Suspense fallback={null}>
              <ObrigadoClient />
            </Suspense>
          </div>
        </div>
      </main>
      <Footer />
    </>
  );
}
