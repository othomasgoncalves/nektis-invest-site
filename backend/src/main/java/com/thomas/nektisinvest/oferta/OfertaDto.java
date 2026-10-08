package com.thomas.nektisinvest.oferta;

import java.time.Instant;

public record OfertaDto(
        int precoCentavos,
        String moeda,
        Instant prazoInscricao,
        boolean inscricoesAbertas) {

    public static OfertaDto de(Oferta oferta, Instant agora) {
        return new OfertaDto(
                oferta.getPrecoCentavos(),
                oferta.getMoeda(),
                oferta.getPrazoInscricao(),
                oferta.inscricoesAbertas(agora));
    }
}
