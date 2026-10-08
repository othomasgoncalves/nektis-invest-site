package com.thomas.nektisinvest.pagamento;

import com.thomas.nektisinvest.assinatura.SituacaoAssinatura;
import java.time.Instant;

public record EventoGateway(
        String id,
        String tipo,
        SituacaoAssinatura situacao,
        String clienteId,
        String assinaturaId,
        String assinaturaLocalId,
        Instant fimPeriodoAtual) {}
