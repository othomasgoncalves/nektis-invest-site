package com.thomas.nektisinvest.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * O frontend exportado pelo Next gera um arquivo por rota: {@code index.html} para a
 * home e {@code obrigado.html} para /obrigado. O Spring Boot serve a home sozinho
 * (welcome page), mas não liga {@code /obrigado} ao {@code obrigado.html} — este
 * forward faz isso, para que o retorno do pagamento da Stripe abra a página em vez
 * de um 404.
 *
 * <p>A query string (?cadastro=…&token=…) sobrevive ao forward e é lida no cliente.
 */
@Controller
class PaginasEstaticasController {

    @GetMapping({"/obrigado", "/obrigado/"})
    String obrigado() {
        return "forward:/obrigado.html";
    }
}
