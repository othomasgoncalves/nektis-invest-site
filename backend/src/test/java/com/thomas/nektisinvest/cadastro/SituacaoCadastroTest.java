package com.thomas.nektisinvest.cadastro;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thomas.nektisinvest.CadastroFixture;
import com.thomas.nektisinvest.TesteIntegracaoBase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SituacaoCadastroTest extends TesteIntegracaoBase {

    @Test
    @DisplayName("Com um token válido, um cadastro novo informa PENDENTE e nenhum link")
    void pendenteSemLinks() throws Exception {
        CadastroFixture.Cadastro cadastro = CadastroFixture.cadastrar(mockMvc, "gi@example.com");

        mockMvc.perform(get("/api/v1/cadastros/{id}/situacao", cadastro.cadastroId())
                        .header("X-Token-Acesso", cadastro.tokenAcesso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("PENDENTE"))
                .andExpect(jsonPath("$.links").doesNotExist());
    }

    @Test
    @DisplayName("Sem o header do token a requisição é rejeitada")
    void tokenAusenteEhRejeitado() throws Exception {
        CadastroFixture.Cadastro cadastro = CadastroFixture.cadastrar(mockMvc, "hugo@example.com");

        mockMvc.perform(get("/api/v1/cadastros/{id}/situacao", cadastro.cadastroId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Um token errado é rejeitado")
    void tokenErradoEhRejeitado() throws Exception {
        CadastroFixture.Cadastro cadastro = CadastroFixture.cadastrar(mockMvc, "iris@example.com");

        mockMvc.perform(get("/api/v1/cadastros/{id}/situacao", cadastro.cadastroId())
                        .header("X-Token-Acesso", "errado"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Um id de cadastro desconhecido é rejeitado como um token inválido")
    void cadastroDesconhecidoEhRejeitado() throws Exception {
        mockMvc.perform(get("/api/v1/cadastros/{id}/situacao", UUID.randomUUID())
                        .header("X-Token-Acesso", "qualquer-coisa"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Acesso negado"));
    }
}
