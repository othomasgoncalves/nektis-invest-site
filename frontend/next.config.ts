import type { NextConfig } from "next";

// Produção: `output: "export"` gera HTML/CSS/JS estáticos em `out/`, que o build
// copia para dentro do jar do Spring Boot (ver .github/workflows/squarecloud.yml).
// Frontend e API passam a viver na mesma origem, então não há CORS nem um
// segundo processo Node para manter de pé.
//
// Os headers de segurança NÃO ficam aqui: `async headers()` só funciona quando
// existe um servidor Next. Quem os envia em produção é o
// HeadersSegurancaFilter do backend.
const nextConfig: NextConfig = {
  output: "export",
  reactStrictMode: true,
  poweredByHeader: false,
  devIndicators: false,
  // O otimizador de imagens do Next é um serviço em runtime; na exportação
  // estática as imagens são servidas como estão.
  images: { unoptimized: true },
  // Libera o acesso ao servidor de dev via IP da rede local (teste em celular).
  // Produção não usa isto: não há servidor de dev nem essa checagem de origem.
  allowedDevOrigins: ["10.1.1.30"],
};

export default nextConfig;
