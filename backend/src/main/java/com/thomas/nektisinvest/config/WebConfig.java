package com.thomas.nektisinvest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final NektisProperties properties;

    public WebConfig(NektisProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
                .allowedOrigins(origemPermitida())
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept", "X-Token-Acesso")
                .maxAge(3600);
    }

    /**
     * Um Origin nunca traz barra final, então um {@code URL_FRONTEND} com barra não
     * casaria com origem nenhuma e derrubaria o cadastro inteiro sem dizer por quê.
     */
    private String origemPermitida() {
        String url = properties.urlFrontend();
        if (url == null) {
            return "";
        }
        String limpa = url.trim();
        while (limpa.endsWith("/")) {
            limpa = limpa.substring(0, limpa.length() - 1);
        }
        return limpa;
    }
}
