"use client";

import { useEffect, useState } from "react";
import { buscarOferta, type Oferta } from "./api";

// A home é estática: este é o valor que vai no HTML gerado no build e o que
// aparece se a API estiver fora do ar. Mantenha em dia com a linha seed de
// `oferta` na V1__init.sql — a API é a fonte da verdade, isto é só o fallback.
export const OFERTA_PADRAO: Oferta = {
  precoCentavos: 31990,
  moeda: "BRL",
  prazoInscricao: "2026-10-26T23:59:59-03:00",
  inscricoesAbertas: true,
};

/**
 * Entrega a oferta padrão na primeira renderização e troca pela da API quando
 * ela responde. O preço e o prazo aparecem na hora, sem esqueleto de carregamento.
 */
export function useOferta(): Oferta {
  const [oferta, setOferta] = useState<Oferta>(OFERTA_PADRAO);

  useEffect(() => {
    let cancelado = false;
    buscarOferta()
      .then((atual) => {
        if (!cancelado) setOferta(atual);
      })
      .catch(() => {
        // Sem rede ou API fora: segue com a OFERTA_PADRAO já renderizada.
      });
    return () => {
      cancelado = true;
    };
  }, []);

  return oferta;
}
