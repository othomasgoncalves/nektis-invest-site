"use client";

import { useEffect, useState, useSyncExternalStore } from "react";
import { CardEtapa } from "@/components/fluxo/CardEtapa";
import { PainelAcesso } from "@/components/fluxo/PainelAcesso";
import {
  ApiErro,
  buscarSituacaoCadastro,
  criarCadastro,
  iniciarPagamento,
  type LinkAcesso,
  type Oferta,
  type SituacaoAssinatura,
} from "@/lib/api";
import { formatarPreco } from "@/lib/formato";
import {
  assinarSessaoCadastro,
  gravarSessaoCadastro,
  snapshotSessaoCadastro,
  snapshotSessaoCadastroServidor,
  type SessaoCadastro,
} from "@/lib/sessao-cadastro";
import { mascararTelefone, paraE164, TELEFONE_PLACEHOLDER } from "@/lib/telefone";
import { cadastroSchema, errosDeCampo, type ErrosCampo } from "@/lib/validacao";

type Etapa = 1 | 2 | 3;

export function FluxoEntrada({ oferta }: { oferta: Oferta }) {
  const sessao = useSyncExternalStore<SessaoCadastro | null>(
    assinarSessaoCadastro,
    snapshotSessaoCadastro,
    snapshotSessaoCadastroServidor,
  );
  const [situacao, setSituacao] = useState<SituacaoAssinatura | null>(null);
  const [links, setLinks] = useState<LinkAcesso[]>([]);

  const etapa: Etapa = situacao === "ATIVA" ? 3 : sessao ? 2 : 1;

  const [form, setForm] = useState({ nome: "", email: "", telefone: "" });
  const [erros, setErros] = useState<ErrosCampo>({});
  const [erroForm, setErroForm] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  const [erroPagamento, setErroPagamento] = useState<string | null>(null);
  const [redirecionando, setRedirecionando] = useState(false);

  useEffect(() => {
    if (!sessao) return;
    let cancelado = false;
    void buscarSituacaoCadastro(sessao.cadastroId, sessao.tokenAcesso)
      .then((resultado) => {
        if (cancelado) return;
        setSituacao(resultado.situacao);
        setLinks(resultado.links ?? []);
      })
      .catch(() => undefined);
    return () => {
      cancelado = true;
    };
  }, [sessao]);

  async function aoCadastrar(evento: React.FormEvent<HTMLFormElement>) {
    evento.preventDefault();
    setErroForm(null);

    const validado = cadastroSchema.safeParse(form);
    if (!validado.success) {
      setErros(errosDeCampo(validado.error));
      return;
    }
    setErros({});
    setEnviando(true);

    try {
      const criado = await criarCadastro({
        nome: validado.data.nome,
        email: validado.data.email,
        telefone: paraE164(validado.data.telefone),
      });
      setSituacao("PENDENTE");
      gravarSessaoCadastro({
        cadastroId: criado.cadastroId,
        tokenAcesso: criado.tokenAcesso,
      });
    } catch (erro) {
      setErroForm(
        erro instanceof ApiErro
          ? erro.message
          : "Não foi possível concluir o cadastro. Tente novamente.",
      );
    } finally {
      setEnviando(false);
    }
  }

  async function aoPagar() {
    if (!sessao) return;
    setErroPagamento(null);
    setRedirecionando(true);
    try {
      const { urlPagamento } = await iniciarPagamento(
        sessao.cadastroId,
        sessao.tokenAcesso,
      );
      window.location.href = urlPagamento;
    } catch (erro) {
      setRedirecionando(false);
      setErroPagamento(
        erro instanceof ApiErro
          ? erro.message
          : "Não foi possível abrir o pagamento. Tente novamente.",
      );
    }
  }

  const preco = formatarPreco(oferta.precoCentavos, oferta.moeda);

  return (
    <section id="cadastro" style={{ paddingBlock: "clamp(72px, 9vw, 110px)" }}>
      <div className="nk-shell">
        <p className="nk-eyebrow">Sua entrada</p>
        <h2 style={{ marginTop: 18, maxWidth: 680 }}>
          Cadastro, pagamento e acesso. Tudo aqui.
        </h2>

        <div
          className="mt-14 grid items-start"
          style={{
            gridTemplateColumns:
              "repeat(auto-fit, minmax(min(100%, 320px), 1fr))",
            gap: "clamp(18px, 2vw, 26px)",
          }}
        >
          <CardEtapa titulo="Cadastro" indice={1} ativo={etapa === 1}>
            <form onSubmit={aoCadastrar} noValidate>
              <div>
                <label className="nk-label" htmlFor="nk-nome">
                  Nome completo
                </label>
                <input
                  id="nk-nome"
                  name="nome"
                  className="nk-field"
                  placeholder="Seu nome"
                  autoComplete="name"
                  disabled={etapa !== 1}
                  aria-invalid={Boolean(erros.nome)}
                  aria-describedby={erros.nome ? "nk-nome-erro" : undefined}
                  value={form.nome}
                  onChange={(e) =>
                    setForm((anterior) => ({ ...anterior, nome: e.target.value }))
                  }
                />
                {erros.nome && (
                  <p className="nk-error" id="nk-nome-erro">
                    {erros.nome}
                  </p>
                )}
              </div>

              <div style={{ marginTop: 18 }}>
                <label className="nk-label" htmlFor="nk-email">
                  E-mail
                </label>
                <input
                  id="nk-email"
                  name="email"
                  type="email"
                  inputMode="email"
                  className="nk-field"
                  placeholder="voce@email.com"
                  autoComplete="email"
                  disabled={etapa !== 1}
                  aria-invalid={Boolean(erros.email)}
                  aria-describedby={erros.email ? "nk-email-erro" : undefined}
                  value={form.email}
                  onChange={(e) =>
                    setForm((anterior) => ({ ...anterior, email: e.target.value }))
                  }
                />
                {erros.email && (
                  <p className="nk-error" id="nk-email-erro">
                    {erros.email}
                  </p>
                )}
              </div>

              <div style={{ marginTop: 18 }}>
                <label className="nk-label" htmlFor="nk-telefone">
                  WhatsApp
                </label>
                <input
                  id="nk-telefone"
                  name="telefone"
                  type="tel"
                  inputMode="tel"
                  className="nk-field"
                  placeholder={TELEFONE_PLACEHOLDER}
                  autoComplete="tel"
                  disabled={etapa !== 1}
                  aria-invalid={Boolean(erros.telefone)}
                  aria-describedby={
                    erros.telefone ? "nk-telefone-erro" : undefined
                  }
                  value={form.telefone}
                  onChange={(e) =>
                    setForm((anterior) => ({
                      ...anterior,
                      telefone: mascararTelefone(e.target.value),
                    }))
                  }
                />
                {erros.telefone && (
                  <p className="nk-error" id="nk-telefone-erro">
                    {erros.telefone}
                  </p>
                )}
              </div>

              {erroForm && (
                <p className="nk-error" role="alert" style={{ marginTop: 14 }}>
                  {erroForm}
                </p>
              )}

              <button
                type="submit"
                className="nk-btn nk-btn-primary nk-btn-block"
                style={{ marginTop: 24 }}
                disabled={etapa !== 1 || enviando || !oferta.inscricoesAbertas}
              >
                {enviando ? "Enviando…" : "Continuar"}
              </button>

              {!oferta.inscricoesAbertas && (
                <p
                  style={{
                    marginTop: 12,
                    fontSize: 13,
                    color: "rgba(62,28,89,.6)",
                  }}
                >
                  As inscrições de lançamento estão encerradas.
                </p>
              )}
            </form>
          </CardEtapa>

          <CardEtapa titulo="Pagamento" indice={2} ativo={etapa === 2}>
            <div
              aria-hidden="true"
              className="flex flex-col justify-between"
              style={{
                background: "var(--color-ink)",
                color: "#fff",
                borderRadius: 18,
                padding: "20px 22px",
                aspectRatio: "16 / 9",
              }}
            >
              <div className="flex items-start justify-between">
                <span
                  style={{
                    fontFamily: "var(--font-display)",
                    fontWeight: 600,
                    fontSize: 15,
                  }}
                >
                  nektis invest
                </span>
                <span
                  style={{
                    width: 10,
                    height: 10,
                    background: "var(--color-accent)",
                    display: "inline-block",
                  }}
                />
              </div>
              <span
                className="tabular-nums"
                style={{ fontSize: 17, letterSpacing: ".14em" }}
              >
                •••• •••• •••• 4821
              </span>
            </div>

            <dl
              style={{
                marginTop: 22,
                paddingTop: 18,
                borderTop: "1px solid rgba(62,28,89,.1)",
                margin: 0,
              }}
            >
              <div className="flex items-baseline justify-between gap-4">
                <dt style={{ fontSize: 15, color: "rgba(62,28,89,.7)" }}>
                  Assinatura mensal
                </dt>
                <dd style={{ margin: 0, fontSize: 15, fontWeight: 700 }}>
                  {preco}
                </dd>
              </div>
              <div
                className="flex items-baseline justify-between gap-4"
                style={{ marginTop: 12 }}
              >
                <dt style={{ fontSize: 15, color: "rgba(62,28,89,.7)" }}>
                  Próxima cobrança
                </dt>
                <dd
                  style={{ margin: 0, fontSize: 15, color: "rgba(62,28,89,.6)" }}
                >
                  em 30 dias
                </dd>
              </div>
            </dl>

            {erroPagamento && (
              <p className="nk-error" role="alert" style={{ marginTop: 14 }}>
                {erroPagamento}
              </p>
            )}

            <button
              type="button"
              onClick={aoPagar}
              className="nk-btn nk-btn-primary nk-btn-block"
              style={{ marginTop: 22 }}
              disabled={etapa !== 2 || !sessao || redirecionando}
            >
              {redirecionando ? "Abrindo pagamento…" : "Confirmar assinatura"}
            </button>
          </CardEtapa>

          <CardEtapa
            titulo="Acesso liberado"
            indice={3}
            ativo={etapa === 3}
            tom="lilas"
          >
            {etapa === 3 ? (
              <PainelAcesso links={links} />
            ) : (
              <p
                style={{
                  fontSize: 15,
                  lineHeight: 1.55,
                  color: "rgba(62,28,89,.7)",
                }}
              >
                {situacao === "INADIMPLENTE"
                  ? "O pagamento não foi concluído. Refaça a assinatura para liberar o acesso."
                  : "Os links dos canais aparecem aqui assim que o pagamento for confirmado."}
              </p>
            )}
          </CardEtapa>
        </div>
      </div>
    </section>
  );
}
