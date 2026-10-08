package com.thomas.nektisinvest;

import com.thomas.nektisinvest.acesso.LinkAcessoRepository;
import com.thomas.nektisinvest.assinatura.AssinaturaRepository;
import com.thomas.nektisinvest.cadastro.AssinanteRepository;
import com.thomas.nektisinvest.pagamento.EventoPagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Container Postgres compartilhado mais um estado limpo antes de cada teste. O
 * container é estático, então todas as classes de teste reusam a mesma
 * instância do banco.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class TesteIntegracaoBase {

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16-alpine");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AssinanteRepository assinantes;

    @Autowired
    protected AssinaturaRepository assinaturas;

    @Autowired
    protected EventoPagamentoRepository eventosPagamento;

    @Autowired
    protected LinkAcessoRepository linksAcesso;

    @BeforeEach
    void limparBanco() {
        eventosPagamento.deleteAll();
        assinaturas.deleteAll();
        assinantes.deleteAll();
    }
}
