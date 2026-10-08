package com.thomas.nektisinvest.notificacao;

import com.thomas.nektisinvest.config.NektisProperties;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "nektis.email.provedor", havingValue = "resend", matchIfMissing = true)
public class ResendEnviadorEmail implements EnviadorEmail {

    private static final Logger log = LoggerFactory.getLogger(ResendEnviadorEmail.class);
    private static final String ENDPOINT = "https://api.resend.com/emails";

    private final RestClient restClient;
    private final String remetente;

    public ResendEnviadorEmail(NektisProperties properties, RestClient.Builder builder) {
        String chave = properties.email().chaveResend();
        if (chave == null || chave.isBlank()) {
            throw new IllegalStateException(
                    "RESEND_API_KEY é obrigatória quando o provedor de e-mail resend está ativo.");
        }
        this.remetente = properties.email().remetente();
        this.restClient = builder
                .baseUrl(ENDPOINT)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + chave)
                .build();
    }

    @Override
    public void enviar(String para, String assunto, String html) {
        Map<String, Object> corpo = Map.of(
                "from", remetente,
                "to", List.of(para),
                "subject", assunto,
                "html", html);
        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException excecao) {
            log.error("Envio do Resend para {} falhou: {}", para, excecao.getMessage());
        }
    }
}
