package com.thomas.nektisinvest.notificacao;

import static org.assertj.core.api.Assertions.assertThat;

import com.thomas.nektisinvest.config.NektisProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * O perfil dev e os testes de integração usam o provedor de e-mail {@code log}, então
 * o {@link ResendEnviadorEmail} só era construído em produção — e uma dependência
 * faltando no classpath derrubava a aplicação na subida, longe de qualquer teste.
 *
 * <p>Este teste monta o bean com a mesma autoconfiguração que o Spring Boot usa, para
 * que a falta do módulo do {@code RestClient.Builder} apareça aqui.
 */
class ResendEnviadorEmailTest {

    private static final NektisProperties COM_CHAVE = propriedades("re_chave_de_teste");

    private final ApplicationContextRunner contexto = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class));

    @Test
    void constroiComORestClientBuilderDoAutoconfigure() {
        contexto.withBean(NektisProperties.class, () -> COM_CHAVE)
                .withBean(ResendEnviadorEmail.class)
                .run(ctx -> assertThat(ctx)
                        .hasNotFailed()
                        .hasSingleBean(ResendEnviadorEmail.class));
    }

    @Test
    void recusaSubirSemAChaveDaResend() {
        contexto.withBean(NektisProperties.class, () -> propriedades("  "))
                .withBean(ResendEnviadorEmail.class)
                .run(ctx -> assertThat(ctx)
                        .getFailure()
                        .rootCause()
                        .hasMessageContaining("RESEND_API_KEY"));
    }

    private static NektisProperties propriedades(String chaveResend) {
        return new NektisProperties(
                null,
                null,
                null,
                null,
                null,
                new NektisProperties.Email(
                        "resend", "Nektis Invest <invest@nektis.tech>", chaveResend),
                null);
    }
}
