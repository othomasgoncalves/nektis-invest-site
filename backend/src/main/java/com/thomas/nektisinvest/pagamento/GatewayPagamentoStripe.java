package com.thomas.nektisinvest.pagamento;

import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.thomas.nektisinvest.assinatura.Assinatura;
import com.thomas.nektisinvest.assinatura.SituacaoAssinatura;
import com.thomas.nektisinvest.cadastro.Assinante;
import com.thomas.nektisinvest.comum.ApiException;
import com.thomas.nektisinvest.config.NektisProperties;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(
        name = "nektis.pagamentos.gateway",
        havingValue = "stripe",
        matchIfMissing = true)
public class GatewayPagamentoStripe implements GatewayPagamento {

    public static final String NOME = "stripe";

    static final String CHAVE_METADADO = "nektis_assinatura_id";

    private static final Logger log = LoggerFactory.getLogger(GatewayPagamentoStripe.class);

    private final StripeClient client;
    private final String idPreco;
    private final String segredoWebhook;
    private final ObjectMapper objectMapper;

    public GatewayPagamentoStripe(NektisProperties properties, ObjectMapper objectMapper) {
        NektisProperties.Pagamentos.Stripe stripe = properties.pagamentos().stripe();
        if (vazio(stripe.chaveSecreta()) || vazio(stripe.idPreco())) {
            throw new IllegalStateException(
                    "STRIPE_SECRET_KEY e STRIPE_PRICE_ID são obrigatórias quando o gateway "
                            + "stripe está ativo.");
        }
        this.client = new StripeClient(stripe.chaveSecreta());
        this.idPreco = stripe.idPreco();
        this.segredoWebhook = stripe.segredoWebhook();
        this.objectMapper = objectMapper;
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

        String localId = assinatura.getId().toString();
        try {
            String clienteId = assinatura.getGatewayClienteId();
            if (clienteId == null) {
                Customer cliente = client.customers().create(CustomerCreateParams.builder()
                        .setEmail(assinante.getEmail())
                        .setName(assinante.getNome())
                        .setPhone(assinante.getTelefone())
                        .putMetadata(CHAVE_METADADO, localId)
                        .build());
                clienteId = cliente.getId();
            }

            Session sessao = client.checkout().sessions().create(SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomer(clienteId)
                    .setSuccessUrl(urlSucesso)
                    .setCancelUrl(urlCancelamento)
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPrice(idPreco)
                            .setQuantity(1L)
                            .build())
                    .putMetadata(CHAVE_METADADO, localId)
                    .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                            .putMetadata(CHAVE_METADADO, localId)
                            .build())
                    .setClientReferenceId(localId)
                    .build());

            return new SessaoPagamento(sessao.getUrl(), clienteId, null);
        } catch (StripeException excecao) {
            log.error("Pagamento da Stripe falhou para a assinatura {}", localId, excecao);
            throw new ApiException(
                    HttpStatus.BAD_GATEWAY,
                    "Pagamento indisponível",
                    "Não foi possível abrir o pagamento. Tente novamente em instantes.");
        }
    }

    @Override
    public Optional<EventoGateway> lerWebhook(String payload, String headerAssinatura) {
        if (vazio(segredoWebhook)) {
            throw new WebhookInvalidoException("STRIPE_WEBHOOK_SECRET não está configurada.");
        }
        try {
            Webhook.constructEvent(payload, headerAssinatura, segredoWebhook);
        } catch (SignatureVerificationException excecao) {
            throw new WebhookInvalidoException("Assinatura da Stripe inválida.", excecao);
        }

        JsonNode raiz;
        try {
            raiz = objectMapper.readTree(payload);
        } catch (Exception excecao) {
            throw new WebhookInvalidoException("Payload da Stripe malformado.", excecao);
        }

        String eventoId = texto(raiz, "id");
        String tipo = texto(raiz, "type");
        JsonNode objeto = raiz.path("data").path("object");

        SituacaoAssinatura situacao = switch (tipo) {
            case "checkout.session.completed", "invoice.paid" -> SituacaoAssinatura.ATIVA;
            case "invoice.payment_failed" -> SituacaoAssinatura.INADIMPLENTE;
            case "customer.subscription.deleted" -> SituacaoAssinatura.CANCELADA;
            case "customer.subscription.updated" -> situacaoDaStripe(texto(objeto, "status"));
            default -> null;
        };
        if (situacao == null) {
            log.debug("Ignorando evento {} da Stripe do tipo {}", eventoId, tipo);
            return Optional.empty();
        }

        return Optional.of(new EventoGateway(
                eventoId,
                tipo,
                situacao,
                clienteId(objeto),
                assinaturaId(objeto, tipo),
                localIdDoMetadado(objeto),
                fimPeriodo(objeto)));
    }

    private static SituacaoAssinatura situacaoDaStripe(String situacaoStripe) {
        if (situacaoStripe == null) {
            return null;
        }
        return switch (situacaoStripe) {
            case "active", "trialing" -> SituacaoAssinatura.ATIVA;
            case "past_due", "unpaid", "incomplete" -> SituacaoAssinatura.INADIMPLENTE;
            case "canceled", "incomplete_expired" -> SituacaoAssinatura.CANCELADA;
            default -> null;
        };
    }

    private static String clienteId(JsonNode objeto) {
        return texto(objeto, "customer");
    }

    private static String assinaturaId(JsonNode objeto, String tipo) {
        if (tipo.startsWith("customer.subscription.")) {
            return texto(objeto, "id");
        }
        String direto = texto(objeto, "subscription");
        if (direto != null) {
            return direto;
        }
        return texto(objeto.path("parent").path("subscription_details"), "subscription");
    }

    private static String localIdDoMetadado(JsonNode objeto) {
        String doMetadado = texto(objeto.path("metadata"), CHAVE_METADADO);
        if (doMetadado != null) {
            return doMetadado;
        }
        String referencia = texto(objeto, "client_reference_id");
        if (referencia != null) {
            return referencia;
        }
        return texto(
                objeto.path("parent").path("subscription_details").path("metadata"),
                CHAVE_METADADO);
    }

    private static Instant fimPeriodo(JsonNode objeto) {
        for (JsonNode candidato : new JsonNode[] {
            objeto.path("current_period_end"),
            objeto.path("items").path("data").path(0).path("current_period_end"),
            objeto.path("lines").path("data").path(0).path("period").path("end")
        }) {
            if (candidato.isNumber()) {
                return Instant.ofEpochSecond(candidato.asLong());
            }
        }
        return null;
    }

    private static String texto(JsonNode node, String campo) {
        JsonNode valor = node.path(campo);
        return valor.isString() && !valor.asString().isBlank() ? valor.asString() : null;
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
