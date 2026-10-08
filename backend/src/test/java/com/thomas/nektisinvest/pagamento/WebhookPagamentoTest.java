package com.thomas.nektisinvest.pagamento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thomas.nektisinvest.CadastroFixture;
import com.thomas.nektisinvest.TesteIntegracaoBase;
import com.thomas.nektisinvest.assinatura.Assinatura;
import com.thomas.nektisinvest.assinatura.SituacaoAssinatura;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class WebhookPagamentoTest extends TesteIntegracaoBase {

    @Test
    @DisplayName("Um webhook pago ativa a assinatura e libera os links")
    void ativaAssinatura() throws Exception {
        CadastroFixture.Cadastro cadastro =
                CadastroFixture.cadastrar(mockMvc, "dani@example.com");
        String assinaturaId =
                CadastroFixture.assinaturaIdDe(CadastroFixture.pagar(mockMvc, cadastro));

        mockMvc.perform(post("/api/v1/webhooks/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CadastroFixture.payloadWebhook("evt_1", assinaturaId, "ATIVA")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/cadastros/{id}/situacao", cadastro.cadastroId())
                        .header("X-Token-Acesso", cadastro.tokenAcesso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("ATIVA"))
                .andExpect(jsonPath("$.links.length()").value(2))
                .andExpect(jsonPath("$.links[0].canal").value("NOTICIAS"))
                .andExpect(jsonPath("$.links[1].canal").value("NETWORKING"));
    }

    @Test
    @DisplayName("Reenviar o mesmo id de evento não muda nada")
    void webhookEhIdempotente() throws Exception {
        CadastroFixture.Cadastro cadastro =
                CadastroFixture.cadastrar(mockMvc, "edu@example.com");
        String assinaturaId =
                CadastroFixture.assinaturaIdDe(CadastroFixture.pagar(mockMvc, cadastro));
        String payload = CadastroFixture.payloadWebhook("evt_repetido", assinaturaId, "ATIVA");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/v1/webhooks/pagamentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isOk());
        }

        assertThat(eventosPagamento.count()).isEqualTo(1);
        assertThat(assinaturas.findAll())
                .singleElement()
                .extracting(Assinatura::getSituacao)
                .isEqualTo(SituacaoAssinatura.ATIVA);
    }

    @Test
    @DisplayName("Um pagamento falho leva a assinatura para INADIMPLENTE e retém os links")
    void pagamentoFalhoRetemLinks() throws Exception {
        CadastroFixture.Cadastro cadastro =
                CadastroFixture.cadastrar(mockMvc, "fabio@example.com");
        String assinaturaId =
                CadastroFixture.assinaturaIdDe(CadastroFixture.pagar(mockMvc, cadastro));

        mockMvc.perform(post("/api/v1/webhooks/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CadastroFixture.payloadWebhook(
                                "evt_2", assinaturaId, "INADIMPLENTE")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/cadastros/{id}/situacao", cadastro.cadastroId())
                        .header("X-Token-Acesso", cadastro.tokenAcesso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("INADIMPLENTE"))
                .andExpect(jsonPath("$.links").doesNotExist());
    }

    @Test
    @DisplayName("Um evento de assinatura desconhecida é aceito, mas não aplicado a nada")
    void assinaturaDesconhecidaEhIgnorada() throws Exception {
        mockMvc.perform(post("/api/v1/webhooks/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CadastroFixture.payloadWebhook(
                                "evt_orfao", "11111111-1111-1111-1111-111111111111", "ATIVA")))
                .andExpect(status().isOk());

        assertThat(assinaturas.count()).isZero();
    }
}
