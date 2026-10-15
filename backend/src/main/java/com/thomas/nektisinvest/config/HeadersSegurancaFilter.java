package com.thomas.nektisinvest.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Headers de segurança de todas as respostas — HTML estático e API.
 *
 * <p>Em desenvolvimento quem fazia isto era o {@code async headers()} do next.config.ts.
 * Com o frontend exportado como arquivos estáticos dentro do jar não existe mais
 * servidor Next em produção, então o backend assume o papel.
 */
@Component
@Order(0)
public class HeadersSegurancaFilter extends OncePerRequestFilter {

    // 'unsafe-inline' em script-src é exigido pelo Next: o HTML exportado carrega
    // os dados de hidratação em tags <script> inline. Sem um servidor para gerar
    // um nonce por requisição, não há como restringir mais sem quebrar a página.
    private static final String CSP_BASE =
            "default-src 'self'; "
                    + "script-src 'self' 'unsafe-inline'; "
                    + "style-src 'self' 'unsafe-inline'; "
                    + "img-src 'self' data: blob:; "
                    + "font-src 'self' data:; "
                    + "connect-src 'self'; "
                    + "object-src 'none'; "
                    + "base-uri 'self'; "
                    + "form-action 'self'; "
                    + "frame-ancestors 'none'";

    private final String csp;

    public HeadersSegurancaFilter(NektisProperties properties) {
        String frameSrc = properties.cspFrameSrc();
        this.csp = CSP_BASE
                + "; frame-src 'self'"
                + (frameSrc == null || frameSrc.isBlank() ? "" : " " + frameSrc.trim());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest requisicao,
            @NonNull HttpServletResponse resposta,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        resposta.setHeader("Content-Security-Policy", csp);
        resposta.setHeader("X-Content-Type-Options", "nosniff");
        resposta.setHeader("X-Frame-Options", "DENY");
        resposta.setHeader("Referrer-Policy", "no-referrer");
        resposta.setHeader(
                "Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()");
        resposta.setHeader(
                "Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload");

        chain.doFilter(requisicao, resposta);
    }
}
