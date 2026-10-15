package com.thomas.nektisinvest.pagamento;

import static org.assertj.core.api.Assertions.assertThat;

import com.thomas.nektisinvest.config.NektisProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * O outro bean que só existe em produção: o perfil dev usa o
 * {@link GatewayPagamentoFalso}. Monta o gateway com a autoconfiguração do
 * {@code ObjectMapper}, para que uma dependência faltando no classpath apareça
 * aqui em vez de na subida em produção.
 */
class GatewayPagamentoStripeTest {

    private final ApplicationContextRunner contexto = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class));

    @Test
    void constroiComOObjectMapperDoAutoconfigure() {
        contexto.withBean(NektisProperties.class, () -> propriedades("sk_teste", "price_teste"))
                .withBean(GatewayPagamentoStripe.class)
                .run(ctx -> assertThat(ctx)
                        .hasNotFailed()
                        .hasSingleBean(GatewayPagamentoStripe.class));
    }

    @Test
    void recusaSubirSemAsChavesDaStripe() {
        contexto.withBean(NektisProperties.class, () -> propriedades("sk_teste", ""))
                .withBean(GatewayPagamentoStripe.class)
                .run(ctx -> assertThat(ctx)
                        .getFailure()
                        .rootCause()
                        .hasMessageContaining("STRIPE_PRICE_ID"));
    }

    private static NektisProperties propriedades(String chaveSecreta, String idPreco) {
        return new NektisProperties(
                null,
                null,
                null,
                null,
                new NektisProperties.Pagamentos(
                        "stripe",
                        new NektisProperties.Pagamentos.Stripe(
                                chaveSecreta, idPreco, "whsec_teste")),
                null,
                null);
    }
}
