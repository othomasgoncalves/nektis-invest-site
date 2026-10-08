package com.thomas.nektisinvest.cadastro;

import com.thomas.nektisinvest.acesso.LinkAcessoService;
import com.thomas.nektisinvest.assinatura.Assinatura;
import com.thomas.nektisinvest.assinatura.AssinaturaRepository;
import com.thomas.nektisinvest.assinatura.SituacaoAssinatura;
import com.thomas.nektisinvest.comum.ApiException;
import com.thomas.nektisinvest.config.NektisProperties;
import com.thomas.nektisinvest.oferta.OfertaService;
import com.thomas.nektisinvest.pagamento.GatewayPagamento;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroService {

    private final AssinanteRepository assinantes;
    private final AssinaturaRepository assinaturas;
    private final TokenAcessoService tokens;
    private final OfertaService ofertas;
    private final GatewayPagamento gateway;
    private final LinkAcessoService linksAcesso;
    private final NektisProperties properties;
    private final Clock relogio;

    public CadastroService(
            AssinanteRepository assinantes,
            AssinaturaRepository assinaturas,
            TokenAcessoService tokens,
            OfertaService ofertas,
            GatewayPagamento gateway,
            LinkAcessoService linksAcesso,
            NektisProperties properties,
            Clock relogio) {
        this.assinantes = assinantes;
        this.assinaturas = assinaturas;
        this.tokens = tokens;
        this.ofertas = ofertas;
        this.gateway = gateway;
        this.linksAcesso = linksAcesso;
        this.properties = properties;
        this.relogio = relogio;
    }

    @Transactional
    public CadastroDto cadastrar(CadastroEntradaDto entrada) {
        if (!ofertas.inscricoesAbertas()) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Inscrições encerradas",
                    "As inscrições de lançamento estão encerradas.");
        }

        String email = entrada.email().trim().toLowerCase();
        String token = tokens.gerar();
        String hash = tokens.hash(token);
        Instant agora = Instant.now(relogio);

        Assinante assinante = assinantes.findByEmailIgnoreCase(email)
                .map(existente -> {
                    if (assinaturas.existsByAssinanteIdAndSituacao(
                            existente.getId(), SituacaoAssinatura.ATIVA)) {
                        throw new ApiException(
                                HttpStatus.CONFLICT,
                                "Assinatura já ativa",
                                "Este e-mail já tem uma assinatura ativa. "
                                        + "Verifique a sua caixa de entrada.");
                    }
                    existente.atualizar(entrada.nome().trim(), entrada.telefone().trim(), hash);
                    return existente;
                })
                .orElseGet(() -> assinantes.save(new Assinante(
                        entrada.nome().trim(), email, entrada.telefone().trim(), hash, agora)));

        return new CadastroDto(assinante.getId(), token);
    }

    @Transactional
    public PagamentoDto pagar(UUID cadastroId, String tokenAcesso) {
        Assinante assinante = autenticar(cadastroId, tokenAcesso);

        if (!ofertas.inscricoesAbertas()) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Inscrições encerradas",
                    "As inscrições de lançamento estão encerradas.");
        }

        Instant agora = Instant.now(relogio);
        Assinatura assinatura = assinaturas
                .findFirstByAssinanteIdOrderByCriadoEmDesc(assinante.getId())
                .orElseGet(() -> assinaturas.save(
                        new Assinatura(assinante, gateway.nome(), agora)));

        if (assinatura.isAtiva()) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Assinatura já ativa",
                    "Esta assinatura já está ativa.");
        }
        assinatura.reabrir(agora);

        String frontend = semBarraFinal(properties.urlFrontend());
        String urlSucesso = frontend + "/obrigado?cadastro=" + cadastroId
                + "&token=" + URLEncoder.encode(tokenAcesso, StandardCharsets.UTF_8);
        String urlCancelamento = frontend + "/#cadastro";

        GatewayPagamento.SessaoPagamento sessao =
                gateway.criarSessaoPagamento(assinante, assinatura, urlSucesso, urlCancelamento);
        assinatura.vincularGateway(sessao.clienteId(), sessao.assinaturaId(), agora);

        return new PagamentoDto(sessao.urlPagamento());
    }

    @Transactional(readOnly = true)
    public SituacaoCadastroDto situacao(UUID cadastroId, String tokenAcesso) {
        Assinante assinante = autenticar(cadastroId, tokenAcesso);

        SituacaoAssinatura situacao = assinaturas
                .findFirstByAssinanteIdOrderByCriadoEmDesc(assinante.getId())
                .map(Assinatura::getSituacao)
                .orElse(SituacaoAssinatura.PENDENTE);

        if (situacao != SituacaoAssinatura.ATIVA) {
            return SituacaoCadastroDto.semLinks(situacao);
        }

        List<SituacaoCadastroDto.Link> links = linksAcesso.linksAtivos().stream()
                .map(link -> new SituacaoCadastroDto.Link(link.getCanal(), link.getUrl()))
                .toList();
        return new SituacaoCadastroDto(situacao, links);
    }

    private Assinante autenticar(UUID cadastroId, String tokenAcesso) {
        return assinantes.findById(cadastroId)
                .filter(assinante -> tokens.confere(tokenAcesso, assinante.getHashTokenAcesso()))
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED,
                        "Acesso negado",
                        "Cadastro não encontrado ou token inválido."));
    }

    private static String semBarraFinal(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
