"use client";

import Link from "next/link";
import { useEffect, useMemo, useState, useSyncExternalStore } from "react";
import { CardEtapa } from "@/components/fluxo/CardEtapa";
import { PainelAcesso } from "@/components/fluxo/PainelAcesso";
import {
  buscarSituacaoCadastro,
  type LinkAcesso,
  type SituacaoAssinatura,
} from "@/lib/api";
import {
  assinarSessaoCadastro,
  gravarSessaoCadastro,
  snapshotSessaoCadastro,
  snapshotSessaoCadastroServidor,
} from "@/lib/sessao-cadastro";

const INTERVALO_CONSULTA_MS = 3_000;
const LIMITE_CONSULTA_MS = 120_000;

type Fase = "consultando" | "ativa" | "expirada" | "falhou" | "invalida";

const semAssinatura = () => () => undefined;
const montadoNoCliente = () => true;
const montadoNoServidor = () => false;

export function ObrigadoClient({
  cadastroId,
  tokenAcesso,
}: {
  cadastroId: string;
  tokenAcesso: string;
}) {
  const [fase, setFase] = useState<Fase>("consultando");
  const [links, setLinks] = useState<LinkAcesso[]>([]);

  const montado = useSyncExternalStore(
    semAssinatura,
    montadoNoCliente,
    montadoNoServidor,
  );
  const guardada = useSyncExternalStore(
    assinarSessaoCadastro,
    snapshotSessaoCadastro,
    snapshotSessaoCadastroServidor,
  );

  const sessao = useMemo(() => {
    if (cadastroId && tokenAcesso) return { cadastroId, tokenAcesso };
    return guardada;
  }, [cadastroId, tokenAcesso, guardada]);

  useEffect(() => {
    if (!cadastroId || !tokenAcesso) return;
    gravarSessaoCadastro({ cadastroId, tokenAcesso });
    window.history.replaceState(null, "", window.location.pathname);
  }, [cadastroId, tokenAcesso]);

  useEffect(() => {
    if (!sessao) return;
    const { cadastroId: id, tokenAcesso: token } = sessao;

    let cancelado = false;
    let temporizador: number | undefined;
    const iniciadoEm = Date.now();

    const resolver = (situacao: SituacaoAssinatura) => {
      if (situacao === "ATIVA") {
        setFase("ativa");
        return true;
      }
      if (situacao === "CANCELADA" || situacao === "INADIMPLENTE") {
        setFase("falhou");
        return true;
      }
      return false;
    };

    const consultar = async () => {
      if (cancelado) return;
      try {
        const resultado = await buscarSituacaoCadastro(id, token);
        if (cancelado) return;
        setLinks(resultado.links ?? []);
        if (resolver(resultado.situacao)) return;
      } catch {
        if (cancelado) return;
      }
      if (cancelado) return;
      if (Date.now() - iniciadoEm >= LIMITE_CONSULTA_MS) {
        setFase("expirada");
        return;
      }
      temporizador = window.setTimeout(consultar, INTERVALO_CONSULTA_MS);
    };

    void consultar();
    return () => {
      cancelado = true;
      if (temporizador) window.clearTimeout(temporizador);
    };
  }, [sessao]);

  const faseAtual: Fase = sessao
    ? fase
    : montado
      ? "invalida"
      : "consultando";

  return (
    <div
      className="mx-auto"
      style={{ maxWidth: 440, width: "100%" }}
      aria-live="polite"
    >
      <CardEtapa titulo="Acesso liberado" indice={3} ativo tom="lilas">
        {faseAtual === "ativa" ? (
          <PainelAcesso links={links} />
        ) : (
          <p
            style={{
              fontSize: 15,
              lineHeight: 1.55,
              color: "rgba(62,28,89,.72)",
            }}
          >
            {
              {
                consultando:
                  "Confirmando o seu pagamento… isso leva alguns segundos.",
                expirada:
                  "O pagamento ainda está sendo processado. Assim que for confirmado, enviamos os links para o seu e-mail.",
                falhou:
                  "O pagamento não foi concluído. Refaça a assinatura para liberar o acesso.",
                invalida:
                  "Link inválido. Volte ao site e refaça o cadastro para continuar.",
                ativa: "",
              }[faseAtual]
            }
          </p>
        )}
      </CardEtapa>

      {faseAtual !== "ativa" && (
        <p className="text-center" style={{ marginTop: 22 }}>
          <Link
            href="/#cadastro"
            style={{ fontSize: 15, color: "rgba(62,28,89,.7)" }}
          >
            Voltar para o site
          </Link>
        </p>
      )}
    </div>
  );
}
