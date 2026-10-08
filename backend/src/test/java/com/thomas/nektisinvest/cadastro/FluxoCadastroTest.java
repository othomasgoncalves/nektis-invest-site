package com.thomas.nektisinvest.cadastro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thomas.nektisinvest.CadastroFixture;
import com.thomas.nektisinvest.TesteIntegracaoBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class FluxoCadastroTest extends TesteIntegracaoBase {

    @Test
    @DisplayName("GET /oferta informa a oferta de lançamento semeada como aberta")
    void ofertaEhPublica() throws Exception {
        mockMvc.perform(get("/api/v1/oferta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precoCentavos").value(31990))
                .andExpect(jsonPath("$.moeda").value("BRL"))
                .andExpect(jsonPath("$.inscricoesAbertas").value(true));
    }

    @Test
    @DisplayName("POST /cadastros cria um assinante e devolve um token de acesso")
    void criaCadastro() throws Exception {
        mockMvc.perform(post("/api/v1/cadastros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CadastroFixture.corpo(
                                "Ana Ribeiro", "ana@example.com", "+244900000001")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cadastroId").isNotEmpty())
                .andExpect(jsonPath("$.tokenAcesso").isNotEmpty());

        assertThat(assinantes.findByEmailIgnoreCase("ana@example.com")).isPresent();
    }

    @Test
    @DisplayName("POST /cadastros rejeita um payload inválido com um ProblemDetail")
    void rejeitaPayloadInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/cadastros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CadastroFixture.corpo("A", "nao-e-email", "12345")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /cadastros reusa um assinante cujo e-mail não tem assinatura ativa")
    void reusaAssinanteSemAssinaturaAtiva() throws Exception {
        CadastroFixture.Cadastro primeiro =
                CadastroFixture.cadastrar(mockMvc, "ana@example.com");
        CadastroFixture.Cadastro segundo =
                CadastroFixture.cadastrar(mockMvc, "ana@example.com");

        assertThat(segundo.cadastroId()).isEqualTo(primeiro.cadastroId());
        // O token é rotacionado, então o antigo precisa deixar de funcionar.
        assertThat(segundo.tokenAcesso()).isNotEqualTo(primeiro.tokenAcesso());
        assertThat(assinantes.count()).isEqualTo(1);

        mockMvc.perform(get("/api/v1/cadastros/{id}/situacao", primeiro.cadastroId())
                        .header("X-Token-Acesso", primeiro.tokenAcesso()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /cadastros/{id}/pagamento devolve a URL do gateway")
    void abrePagamento() throws Exception {
        CadastroFixture.Cadastro cadastro =
                CadastroFixture.cadastrar(mockMvc, "bruno@example.com");

        mockMvc.perform(post("/api/v1/cadastros/{id}/pagamento", cadastro.cadastroId())
                        .header("X-Token-Acesso", cadastro.tokenAcesso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.urlPagamento").value(
                        org.hamcrest.Matchers.containsString("/api/v1/dev/pagamento")));

        assertThat(assinaturas.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("O pagamento recusa um token que não pertence ao cadastro")
    void pagamentoRejeitaTokenErrado() throws Exception {
        CadastroFixture.Cadastro cadastro =
                CadastroFixture.cadastrar(mockMvc, "carla@example.com");

        mockMvc.perform(post("/api/v1/cadastros/{id}/pagamento", cadastro.cadastroId())
                        .header("X-Token-Acesso", "nao-e-o-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Acesso negado"));
    }
}
