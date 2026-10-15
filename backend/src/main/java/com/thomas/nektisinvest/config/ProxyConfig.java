package com.thomas.nektisinvest.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Reconstrói o esquema e o host públicos a partir dos headers X-Forwarded-*.
 *
 * <p>Sem isto, atrás de um proxy que termina o TLS, o Tomcat enxerga a requisição
 * como {@code http://invest.nektis.tech:80} enquanto o navegador manda
 * {@code Origin: https://invest.nektis.tech}. O {@code CorsUtils.isCorsRequest}
 * compara esquema, host e porta, então a diferença no esquema faz o Spring tratar
 * como requisição entre origens o que na verdade é o site falando com a própria
 * API — e a chamada morre na allowlist do CORS.
 *
 * <p>Ordem 2: depois do {@link LimiteTaxaFilter}. O ForwardedHeaderFilter
 * <strong>remove</strong> os X-Forwarded-* depois de aplicá-los, e o rate limit
 * precisa ler o X-Forwarded-For para achar o IP do cliente.
 */
@Configuration
@ConditionalOnProperty(name = "nektis.confiar-proxy", havingValue = "true")
class ProxyConfig {

    @Bean
    FilterRegistrationBean<ForwardedHeaderFilter> headersEncaminhados() {
        FilterRegistrationBean<ForwardedHeaderFilter> registro =
                new FilterRegistrationBean<>(new ForwardedHeaderFilter());
        registro.setOrder(2);
        return registro;
    }
}
