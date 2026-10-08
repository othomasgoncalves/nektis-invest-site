import type { Metadata, Viewport } from "next";
import { satoshi, unbounded } from "./fontes/fontes";
import "./globals.css";

const URL_SITE = "https://invest.nektis.tech";

const TITULO = "Nektis Invest — Onde deixar o seu dinheiro seguro e rentável.";
const DESCRICAO =
  "Um grupo fechado no WhatsApp com a leitura diária do mercado feita por João Pedro: a notícia resumida e a ação a aplicar, sem promessa de dinheiro fácil.";

export const metadata: Metadata = {
  metadataBase: new URL(URL_SITE),
  title: TITULO,
  description: DESCRICAO,
  alternates: { canonical: "/" },
  openGraph: {
    type: "website",
    locale: "pt_BR",
    url: URL_SITE,
    siteName: "Nektis Invest",
    title: TITULO,
    description: DESCRICAO,
    images: [{ url: "/og.png", width: 1200, height: 630, alt: "Nektis Invest" }],
  },
  twitter: {
    card: "summary_large_image",
    title: TITULO,
    description: DESCRICAO,
    images: ["/og.png"],
  },
};

export const viewport: Viewport = {
  themeColor: "#3e1c59",
  width: "device-width",
  initialScale: 1,
};

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="pt-BR" className={`${unbounded.variable} ${satoshi.variable}`}>
      <body>{children}</body>
    </html>
  );
}
