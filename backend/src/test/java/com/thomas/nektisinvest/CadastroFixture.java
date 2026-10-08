package com.thomas.nektisinvest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Atalhos para acionar os endpoints públicos de cadastro nos testes. */
public final class CadastroFixture {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private CadastroFixture() {}

    public record Cadastro(String cadastroId, String tokenAcesso) {}

    public static String corpo(String nome, String email, String telefone) {
        return """
                {"nome":"%s","email":"%s","telefone":"%s"}"""
                .formatted(nome, email, telefone);
    }

    /** Registra um membro e devolve o id dele mais um token de acesso novo. */
    public static Cadastro cadastrar(MockMvc mockMvc, String email) throws Exception {
        String resposta = mockMvc.perform(post("/api/v1/cadastros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Membro de Teste", email, "+244900000000")))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = JSON.readTree(resposta);
        return new Cadastro(
                node.path("cadastroId").asString(), node.path("tokenAcesso").asString());
    }

    /** Abre uma sessão de pagamento e devolve a URL do gateway. */
    public static String pagar(MockMvc mockMvc, Cadastro cadastro) throws Exception {
        String resposta = mockMvc.perform(
                        post("/api/v1/cadastros/{id}/pagamento", cadastro.cadastroId())
                                .header("X-Token-Acesso", cadastro.tokenAcesso()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JSON.readTree(resposta).path("urlPagamento").asString();
    }

    /** O id local da assinatura que o gateway falso codifica na URL de pagamento. */
    public static String assinaturaIdDe(String urlPagamento) {
        int inicio = urlPagamento.indexOf("assinatura=") + "assinatura=".length();
        int fim = urlPagamento.indexOf('&', inicio);
        return urlPagamento.substring(inicio, fim < 0 ? urlPagamento.length() : fim);
    }

    /** Payload de webhook no formato que o gateway falso entende. */
    public static String payloadWebhook(String eventoId, String assinaturaId, String situacao) {
        return """
                {"id":"%s","tipo":"checkout.session.completed","situacao":"%s","assinatura":"%s"}"""
                .formatted(eventoId, situacao, assinaturaId);
    }
}
