package com.thomas.nektisinvest.pagamento;

import com.thomas.nektisinvest.assinatura.Assinatura;
import com.thomas.nektisinvest.cadastro.Assinante;
import java.util.Optional;

public interface GatewayPagamento {

    String nome();

    SessaoPagamento criarSessaoPagamento(
            Assinante assinante, Assinatura assinatura, String urlSucesso, String urlCancelamento);

    Optional<EventoGateway> lerWebhook(String payload, String headerAssinatura);

    record SessaoPagamento(String urlPagamento, String clienteId, String assinaturaId) {}

    class WebhookInvalidoException extends RuntimeException {
        public WebhookInvalidoException(String mensagem, Throwable causa) {
            super(mensagem, causa);
        }

        public WebhookInvalidoException(String mensagem) {
            super(mensagem);
        }
    }
}
