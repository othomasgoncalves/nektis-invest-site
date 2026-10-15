package com.thomas.nektisinvest.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * O site e a API vivem na mesma origem em produção, mas o navegador manda o header
 * Origin em todo POST. Atrás de um proxy que termina o TLS, o Tomcat enxerga
 * {@code http://...} e o Spring passa a tratar o site falando com a própria API
 * como requisição entre origens — foi o que devolveu 403 no cadastro.
 */
class HeadersEncaminhadosTest {

    private static final String HOST = "invest.nektis.tech";
    private static final String ORIGEM = "https://" + HOST;

    @RestController
    static class SondaController {
        Boolean ehCors;

        @PostMapping("/api/v1/sonda")
        void sondar(HttpServletRequest requisicao) {
            this.ehCors = CorsUtils.isCorsRequest(requisicao);
        }
    }

    private final SondaController sonda = new SondaController();

    private MockMvc comFiltro(boolean filtroLigado) {
        var builder = MockMvcBuilders.standaloneSetup(sonda);
        if (filtroLigado) {
            builder = builder.addFilters(new ForwardedHeaderFilter());
        }
        return builder.build();
    }

    @Test
    void semOFiltroOProxyFazOSiteParecerOutraOrigem() throws Exception {
        comFiltro(false).perform(post("/api/v1/sonda"));

        assertThat(sonda.ehCors)
                .as("sem reconstruir o esquema, https:// no Origin não bate com o http:// visto pelo Tomcat")
                .isTrue();
    }

    @Test
    void comOFiltroARequisicaoDeixaDeSerCors() throws Exception {
        comFiltro(true).perform(post("/api/v1/sonda"));

        assertThat(sonda.ehCors)
                .as("mesma origem: o CORS nem chega a ser avaliado")
                .isFalse();
    }

    private static MockHttpServletRequestBuilder post(String caminho) {
        return MockMvcRequestBuilders.post(caminho)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header("Host", HOST)
                .header("Origin", ORIGEM)
                .header("X-Forwarded-Proto", "https")
                .header("X-Forwarded-Host", HOST);
    }
}
