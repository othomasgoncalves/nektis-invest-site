package com.thomas.nektisinvest.pagamento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks/pagamentos")
class WebhookPagamentoController {

    private static final Logger log =
            LoggerFactory.getLogger(WebhookPagamentoController.class);

    private final WebhookPagamentoService service;

    WebhookPagamentoController(WebhookPagamentoService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<Void> receber(
            @RequestBody String payload,
            @RequestHeader(name = "Stripe-Signature", required = false) String assinatura) {
        try {
            service.processar(payload, assinatura);
            return ResponseEntity.ok().build();
        } catch (GatewayPagamento.WebhookInvalidoException excecao) {
            log.warn("Entrega de webhook rejeitada: {}", excecao.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
