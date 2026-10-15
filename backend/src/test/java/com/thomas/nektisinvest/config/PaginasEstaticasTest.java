package com.thomas.nektisinvest.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Garante o contrato que o deploy na Square Cloud depende: /obrigado cai no HTML
 * exportado pelo Next e toda resposta sai com os headers de segurança que antes
 * vinham do next.config.ts.
 */
class PaginasEstaticasTest {

    private static final NektisProperties PROPRIEDADES = new NektisProperties(
            "https://www.youtube-nocookie.com", null, null, null, null, null, null);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new PaginasEstaticasController())
            .addFilters(new HeadersSegurancaFilter(PROPRIEDADES))
            .build();

    @Test
    void obrigadoCaiNoHtmlExportado() throws Exception {
        mockMvc.perform(get("/obrigado").param("cadastro", "abc").param("token", "xyz"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/obrigado.html"));
    }

    @Test
    void obrigadoComBarraFinalTambemCai() throws Exception {
        mockMvc.perform(get("/obrigado/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/obrigado.html"));
    }

    @Test
    void respostaLevaOsHeadersDeSeguranca() throws Exception {
        mockMvc.perform(get("/obrigado"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string(
                        "Strict-Transport-Security",
                        "max-age=63072000; includeSubDomains; preload"))
                // O HTML do Next carrega os dados de hidratação em script inline.
                .andExpect(header().string(
                        "Content-Security-Policy",
                        containsString("script-src 'self' 'unsafe-inline'")))
                .andExpect(header().string(
                        "Content-Security-Policy", containsString("frame-ancestors 'none'")))
                .andExpect(header().string(
                        "Content-Security-Policy",
                        containsString("frame-src 'self' https://www.youtube-nocookie.com")));
    }
}
