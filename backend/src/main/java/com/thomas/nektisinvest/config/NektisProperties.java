package com.thomas.nektisinvest.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nektis")
public record NektisProperties(
        String urlFrontend,
        String urlPublica,
        LimiteTaxa limiteTaxa,
        Pagamentos pagamentos,
        Email email,
        LinksAcesso linksAcesso) {

    public record LimiteTaxa(
            int capacidade, int capacidadeLeitura, Duration janela, boolean confiarProxy) {}

    public record Pagamentos(String gateway, Stripe stripe) {
        public record Stripe(String chaveSecreta, String idPreco, String segredoWebhook) {}
    }

    public record Email(String provedor, String remetente, String chaveResend) {}

    public record LinksAcesso(String noticias, String networking) {}
}
