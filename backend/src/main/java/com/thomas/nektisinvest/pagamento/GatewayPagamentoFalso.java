package com.thomas.nektisinvest.pagamento;

import com.thomas.nektisinvest.assinatura.Assinatura;
import com.thomas.nektisinvest.assinatura.SituacaoAssinatura;
import com.thomas.nektisinvest.cadastro.Assinante;
import com.thomas.nektisinvest.config.NektisProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(name = "nektis.pagamentos.gateway", havingValue = "falso")
public class GatewayPagamentoFalso implements GatewayPagamento {

    public static final String NOME = "falso";

    private final ObjectMapper objectMapper;
    private final String urlPublica;

    public GatewayPagamentoFalso(ObjectMapper objectMapper, NektisProperties properties) {
        this.objectMapper = objectMapper;
        String base = properties.urlPublica();
        this.urlPublica = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    @Override
    public String nome() {
        return NOME;
    }

    @Override
    public SessaoPagamento criarSessaoPagamento(
            Assinante assinante,
            Assinatura assinatura,
            String urlSucesso,
            String urlCancelamento) {

        String urlPagamento = urlPublica + "/api/v1/dev/pagamento"
                + "?assinatura=" + codificar(assinatura.getId().toString())
                + "&sucesso=" + codificar(urlSucesso)
                + "&cancelamento=" + codificar(urlCancelamento);

        return new SessaoPagamento(
                urlPagamento,
                "cus_falso_" + assinatura.getId(),
                "sub_falso_" + assinatura.getId());
    }

    @Override
    public Optional<EventoGateway> lerWebhook(String payload, String headerAssinatura) {
        JsonNode raiz;
        try {
            raiz = objectMapper.readTree(payload);
        } catch (Exception excecao) {
            throw new WebhookInvalidoException("Payload falso malformado.", excecao);
        }

        String tipo = raiz.path("tipo").asString("checkout.session.completed");
        SituacaoAssinatura situacao;
        try {
            situacao = SituacaoAssinatura.valueOf(raiz.path("situacao").asString("ATIVA"));
        } catch (IllegalArgumentException excecao) {
            return Optional.empty();
        }

        String localId = raiz.path("assinatura").asString(null);
        return Optional.of(new EventoGateway(
                raiz.path("id").asString("evt_falso_" + UUID.randomUUID()),
                tipo,
                situacao,
                localId == null ? null : "cus_falso_" + localId,
                localId == null ? null : "sub_falso_" + localId,
                localId,
                Instant.now().plus(30, ChronoUnit.DAYS)));
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}
