package com.thomas.nektisinvest.pagamento;

import com.thomas.nektisinvest.comum.ApiException;
import com.thomas.nektisinvest.config.NektisProperties;
import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev/pagamento")
@ConditionalOnProperty(name = "nektis.pagamentos.gateway", havingValue = "falso")
class PagamentoFalsoController {

    private final WebhookPagamentoService webhooks;
    private final String urlFrontend;

    PagamentoFalsoController(
            WebhookPagamentoService webhooks, NektisProperties properties) {
        this.webhooks = webhooks;
        String base = properties.urlFrontend();
        this.urlFrontend = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String pagina(
            @RequestParam String assinatura,
            @RequestParam String sucesso,
            @RequestParam String cancelamento) {
        validarDestino(sucesso);
        validarDestino(cancelamento);
        return """
                <!doctype html>
                <html lang="pt-BR">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Pagamento simulado — Nektis Invest</title>
                  <style>
                    body { font-family: system-ui, sans-serif; background: #efebe8; color: #3e1c59;
                           display: grid; place-items: center; min-height: 100vh; margin: 0; }
                    .card { background: #fff; border-radius: 24px; padding: 40px; max-width: 420px;
                            box-shadow: 0 24px 60px rgba(62,28,89,.12); }
                    h1 { font-size: 22px; margin: 0 0 12px; }
                    p { color: rgba(62,28,89,.7); line-height: 1.6; margin: 0 0 24px; }
                    button, a { display: block; width: 100%%; box-sizing: border-box; text-align: center;
                                border: 0; border-radius: 999px; padding: 16px; font-size: 15px;
                                font-weight: 700; cursor: pointer; text-decoration: none; }
                    button { background: #3e1c59; color: #fff; }
                    a { background: transparent; color: rgba(62,28,89,.7); margin-top: 8px; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <h1>Pagamento simulado</h1>
                    <p>Gateway <strong>falso</strong> do perfil <code>dev</code>. Confirmar dispara o
                       webhook de pagamento e libera o acesso.</p>
                    <form method="post" action="/api/v1/dev/pagamento">
                      <input type="hidden" name="assinatura" value="%s">
                      <input type="hidden" name="sucesso" value="%s">
                      <button type="submit">Confirmar pagamento</button>
                    </form>
                    <a href="%s">Cancelar</a>
                  </div>
                </body>
                </html>
                """
                .formatted(escapar(assinatura), escapar(sucesso), escapar(cancelamento));
    }

    @PostMapping
    ResponseEntity<Void> confirmar(
            @RequestParam String assinatura, @RequestParam String sucesso) {

        validarDestino(sucesso);

        String payload = """
                {"id":"evt_falso_%s","tipo":"checkout.session.completed",\
                "situacao":"ATIVA","assinatura":"%s"}"""
                .formatted(assinatura, assinatura);

        webhooks.processar(payload, null);

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(sucesso))
                .build();
    }

    private void validarDestino(String url) {
        if (!url.equals(urlFrontend) && !url.startsWith(urlFrontend + "/")) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Destino inválido",
                    "O redirecionamento precisa apontar para o frontend configurado.");
        }
    }

    private static String escapar(String valor) {
        return valor.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
    }
}
