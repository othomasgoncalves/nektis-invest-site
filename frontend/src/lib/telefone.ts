type Pais = {
  ddi: string;
  digitosNacionais: number;
  grupos: number[];
};

const PAISES: Pais[] = [
  { ddi: "244", digitosNacionais: 9, grupos: [3, 3, 3] },
  { ddi: "55", digitosNacionais: 11, grupos: [2, 5, 4] },
];

const MAX_DIGITOS = 15;

export const TELEFONE_PLACEHOLDER = "+244 900 000 000";

function digitosDe(valor: string): string {
  return valor.replace(/\D/g, "").slice(0, MAX_DIGITOS);
}

function paisDe(digitos: string): Pais | undefined {
  return PAISES.find((p) => digitos.startsWith(p.ddi));
}

export function mascararTelefone(valor: string): string {
  const digitos = digitosDe(valor);
  if (!digitos) return "";

  const pais = paisDe(digitos);
  if (!pais) return `+${digitos}`;

  const nacionais = digitos.slice(pais.ddi.length);
  const partes: string[] = [];
  let cursor = 0;

  for (const tamanho of pais.grupos) {
    if (cursor >= nacionais.length) break;
    partes.push(nacionais.slice(cursor, cursor + tamanho));
    cursor += tamanho;
  }
  if (cursor < nacionais.length) partes.push(nacionais.slice(cursor));

  if (pais.ddi === "55" && partes.length > 1) {
    const [area, ...resto] = partes;
    return `+${pais.ddi} (${area}) ${resto.join("-")}`.trimEnd();
  }

  return `+${pais.ddi} ${partes.join(" ")}`.trimEnd();
}

export function paraE164(valor: string): string {
  const digitos = digitosDe(valor);
  return digitos ? `+${digitos}` : "";
}

export function telefoneValido(valor: string): boolean {
  const digitos = digitosDe(valor);
  const pais = paisDe(digitos);
  if (pais) {
    return digitos.length === pais.ddi.length + pais.digitosNacionais;
  }
  return digitos.length >= 8 && digitos.length <= MAX_DIGITOS;
}
