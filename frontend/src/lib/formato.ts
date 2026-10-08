export function formatarPreco(centavos: number, moeda = "BRL"): string {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: moeda,
  }).format(centavos / 100);
}

export function formatarValor(centavos: number): string {
  return new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(centavos / 100);
}

export function formatarPrazo(iso: string): string {
  const data = new Date(iso);
  if (Number.isNaN(data.getTime())) return "";
  return new Intl.DateTimeFormat("pt-BR", {
    day: "numeric",
    month: "long",
    timeZone: "America/Sao_Paulo",
  }).format(data);
}
