package com.thomas.nektisinvest.cadastro;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cadastros")
class CadastroController {

    static final String HEADER_TOKEN = "X-Token-Acesso";

    private final CadastroService service;

    CadastroController(CadastroService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CadastroDto criar(@Valid @RequestBody CadastroEntradaDto entrada) {
        return service.cadastrar(entrada);
    }

    @PostMapping("/{id}/pagamento")
    PagamentoDto pagamento(
            @PathVariable UUID id, @RequestHeader(HEADER_TOKEN) String tokenAcesso) {
        return service.pagar(id, tokenAcesso);
    }

    @GetMapping("/{id}/situacao")
    SituacaoCadastroDto situacao(
            @PathVariable UUID id, @RequestHeader(HEADER_TOKEN) String tokenAcesso) {
        return service.situacao(id, tokenAcesso);
    }
}
