import type { NextConfig } from "next";

const HEADERS_SEGURANCA = [
  { key: "X-Content-Type-Options", value: "nosniff" },
  { key: "X-Frame-Options", value: "DENY" },
  { key: "Referrer-Policy", value: "no-referrer" },
  {
    key: "Strict-Transport-Security",
    value: "max-age=63072000; includeSubDomains; preload",
  },
  {
    key: "Permissions-Policy",
    value: "camera=(), microphone=(), geolocation=(), payment=()",
  },
  {
    key: "Content-Security-Policy",
    value: "frame-ancestors 'none'; base-uri 'self'; form-action 'self'",
  },
];

const nextConfig: NextConfig = {
  output: "standalone",
  reactStrictMode: true,
  poweredByHeader: false,
  devIndicators: false,
  // Libera o acesso ao servidor de dev via IP da rede local (teste em celular).
  // Produção não usa isto: não há servidor de dev nem essa checagem de origem.
  allowedDevOrigins: ["10.1.1.30"],
  async headers() {
    return [{ source: "/:path*", headers: HEADERS_SEGURANCA }];
  },
};

export default nextConfig;
