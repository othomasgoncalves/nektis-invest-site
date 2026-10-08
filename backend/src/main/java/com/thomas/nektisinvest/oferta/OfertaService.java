package com.thomas.nektisinvest.oferta;

import com.thomas.nektisinvest.comum.ApiException;
import java.time.Clock;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfertaService {

    private final OfertaRepository repository;
    private final Clock relogio;

    public OfertaService(OfertaRepository repository, Clock relogio) {
        this.repository = repository;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public Oferta ofertaAtual() {
        return repository.findFirstByAtivoTrueOrderByIdDesc()
                .orElseThrow(() -> new ApiException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Oferta indisponível",
                        "Nenhuma oferta ativa configurada."));
    }

    @Transactional(readOnly = true)
    public OfertaDto ofertaAtualDto() {
        return OfertaDto.de(ofertaAtual(), agora());
    }

    public boolean inscricoesAbertas() {
        return ofertaAtual().inscricoesAbertas(agora());
    }

    private Instant agora() {
        return Instant.now(relogio);
    }
}
