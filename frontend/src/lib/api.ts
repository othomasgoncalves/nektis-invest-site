export type SituacaoAssinatura =
  | "PENDENTE"
  | "ATIVA"
  | "INADIMPLENTE"
  | "CANCELADA";

export type CanalAcesso = "NOTICIAS" | "NETWORKING";

export type Oferta = {
  precoCentavos: number;
  moeda: string;
  prazoInscricao: string;
  inscricoesAbertas: boolean;
};

export type CadastroEntrada = {
  nome: string;
  email: string;
  telefone: string;
};

export type Cadastro = {
  cadastroId: string;
  tokenAcesso: string;
};

export type Pagamento = {
  urlPagamento: string;
};

export type LinkAcesso = {
  canal: CanalAcesso;
  url: string;
};

export type SituacaoCadastro = {
  situacao: SituacaoAssinatura;
  links?: LinkAcesso[];
};

export type DetalheErro = {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
};

export class ApiErro extends Error {
  readonly situacao: number;
  readonly problema: DetalheErro;

  constructor(situacao: number, problema: DetalheErro) {
    super(problema.detail || problema.title || `A requisição falhou (${situacao})`);
    this.name = "ApiErro";
    this.situacao = situacao;
    this.problema = problema;
  }
}

const URL_BASE = (
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1"
).replace(/\/+$/, "");

async function requisicao<T>(caminho: string, init?: RequestInit): Promise<T> {
  let resposta: Response;
  try {
    resposta = await fetch(`${URL_BASE}${caminho}`, {
      ...init,
      headers: {
        Accept: "application/json",
        ...(init?.body ? { "Content-Type": "application/json" } : {}),
        ...init?.headers,
      },
    });
  } catch {
    throw new ApiErro(0, {
      title: "Falha de conexão",
      detail: "Não foi possível falar com o servidor. Tente novamente.",
    });
  }

  if (!resposta.ok) {
    let problema: DetalheErro = {};
    try {
      problema = (await resposta.json()) as DetalheErro;
    } catch {
      problema = { title: resposta.statusText };
    }
    throw new ApiErro(resposta.status, problema);
  }

  if (resposta.status === 204) {
    return undefined as T;
  }

  return (await resposta.json()) as T;
}

export async function buscarOferta(): Promise<Oferta> {
  return requisicao<Oferta>("/oferta", { next: { revalidate: 60 } });
}

export async function criarCadastro(corpo: CadastroEntrada): Promise<Cadastro> {
  return requisicao<Cadastro>("/cadastros", {
    method: "POST",
    body: JSON.stringify(corpo),
  });
}

export async function iniciarPagamento(
  cadastroId: string,
  tokenAcesso: string,
): Promise<Pagamento> {
  return requisicao<Pagamento>(`/cadastros/${cadastroId}/pagamento`, {
    method: "POST",
    headers: { "X-Token-Acesso": tokenAcesso },
  });
}

export async function buscarSituacaoCadastro(
  cadastroId: string,
  tokenAcesso: string,
): Promise<SituacaoCadastro> {
  return requisicao<SituacaoCadastro>(`/cadastros/${cadastroId}/situacao`, {
    headers: { "X-Token-Acesso": tokenAcesso },
    cache: "no-store",
  });
}
