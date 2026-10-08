package com.thomas.nektisinvest.oferta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oferta")
class OfertaController {

    private final OfertaService service;

    OfertaController(OfertaService service) {
        this.service = service;
    }

    @GetMapping
    OfertaDto oferta() {
        return service.ofertaAtualDto();
    }
}
