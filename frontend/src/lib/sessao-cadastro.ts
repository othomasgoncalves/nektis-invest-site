"use client";

const CHAVE = "nektis.invest.cadastro";

export type SessaoCadastro = {
  cadastroId: string;
  tokenAcesso: string;
};

function interpretar(bruto: string | null): SessaoCadastro | null {
  if (!bruto) return null;
  try {
    const interpretado: unknown = JSON.parse(bruto);
    if (
      typeof interpretado === "object" &&
      interpretado !== null &&
      typeof (interpretado as SessaoCadastro).cadastroId === "string" &&
      typeof (interpretado as SessaoCadastro).tokenAcesso === "string"
    ) {
      return interpretado as SessaoCadastro;
    }
  } catch {
    return null;
  }
  return null;
}

function valorBruto(): string | null {
  if (typeof window === "undefined") return null;
  try {
    return window.sessionStorage.getItem(CHAVE);
  } catch {
    return null;
  }
}

export function lerSessaoCadastro(): SessaoCadastro | null {
  return interpretar(valorBruto());
}

const ouvintes = new Set<() => void>();
let brutoEmCache: string | null | undefined;
let valorEmCache: SessaoCadastro | null = null;

function emitir(): void {
  for (const ouvinte of ouvintes) ouvinte();
}

export function assinarSessaoCadastro(ouvinte: () => void): () => void {
  ouvintes.add(ouvinte);
  window.addEventListener("storage", emitir);
  return () => {
    ouvintes.delete(ouvinte);
    if (ouvintes.size === 0) window.removeEventListener("storage", emitir);
  };
}

export function snapshotSessaoCadastro(): SessaoCadastro | null {
  const bruto = valorBruto();
  if (bruto !== brutoEmCache) {
    brutoEmCache = bruto;
    valorEmCache = interpretar(bruto);
  }
  return valorEmCache;
}

export function snapshotSessaoCadastroServidor(): SessaoCadastro | null {
  return null;
}

export function gravarSessaoCadastro(sessao: SessaoCadastro): void {
  if (typeof window === "undefined") return;
  try {
    window.sessionStorage.setItem(CHAVE, JSON.stringify(sessao));
  } catch {
    emitir();
    return;
  }
  emitir();
}

export function limparSessaoCadastro(): void {
  if (typeof window === "undefined") return;
  try {
    window.sessionStorage.removeItem(CHAVE);
  } catch {
    emitir();
    return;
  }
  emitir();
}
