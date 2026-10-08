package com.thomas.nektisinvest.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class LimiteTaxaFilter extends OncePerRequestFilter {

    private record Janela(Instant iniciadaEm, AtomicInteger total) {}

    private static final int MAX_JANELAS = 10_000;

    private final Map<String, Janela> janelas = new ConcurrentHashMap<>();
    private final int capacidade;
    private final int capacidadeLeitura;
    private final Duration janela;
    private final boolean confiarProxy;

    public LimiteTaxaFilter(NektisProperties properties) {
        this.capacidade = properties.limiteTaxa().capacidade();
        this.capacidadeLeitura = properties.limiteTaxa().capacidadeLeitura();
        this.janela = properties.limiteTaxa().janela();
        this.confiarProxy = properties.limiteTaxa().confiarProxy();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest requisicao) {
        if (!requisicao.getRequestURI().startsWith("/api/v1/cadastros")) {
            return true;
        }
        return !escrita(requisicao) && !leitura(requisicao);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest requisicao,
            @NonNull HttpServletResponse resposta,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        boolean escrita = escrita(requisicao);
        String chave = (escrita ? "E:" : "L:") + ipCliente(requisicao);

        if (excedeu(chave, escrita ? capacidade : capacidadeLeitura)) {
            resposta.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            resposta.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            resposta.getWriter().write("""
                    {"type":"about:blank","title":"Too Many Requests","status":429,\
                    "detail":"Muitas tentativas. Aguarde um instante e tente novamente."}""");
            return;
        }
        chain.doFilter(requisicao, resposta);
    }

    private static boolean escrita(HttpServletRequest requisicao) {
        return "POST".equalsIgnoreCase(requisicao.getMethod());
    }

    private static boolean leitura(HttpServletRequest requisicao) {
        return "GET".equalsIgnoreCase(requisicao.getMethod());
    }

    private boolean excedeu(String chave, int limite) {
        Instant agora = Instant.now();
        Janela atual = janelas.compute(chave, (ignorada, existente) -> {
            if (existente == null || existente.iniciadaEm().plus(janela).isBefore(agora)) {
                return new Janela(agora, new AtomicInteger());
            }
            return existente;
        });
        if (janelas.size() > MAX_JANELAS) {
            janelas.entrySet()
                    .removeIf(e -> e.getValue().iniciadaEm().plus(janela).isBefore(agora));
        }
        return atual.total().incrementAndGet() > limite;
    }

    private String ipCliente(HttpServletRequest requisicao) {
        if (!confiarProxy) {
            return requisicao.getRemoteAddr();
        }
        String encaminhado = requisicao.getHeader("X-Forwarded-For");
        if (encaminhado == null || encaminhado.isBlank()) {
            return requisicao.getRemoteAddr();
        }
        int virgula = encaminhado.lastIndexOf(',');
        String ip = (virgula >= 0 ? encaminhado.substring(virgula + 1) : encaminhado).trim();
        return ip.isEmpty() ? requisicao.getRemoteAddr() : ip;
    }
}
