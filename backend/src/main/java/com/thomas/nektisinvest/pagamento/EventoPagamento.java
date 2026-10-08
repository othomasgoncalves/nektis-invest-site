package com.thomas.nektisinvest.pagamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "evento_pagamento")
public class EventoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gateway_evento_id", nullable = false, unique = true)
    private String gatewayEventoId;

    @Column(nullable = false, length = 120)
    private String tipo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "recebido_em", nullable = false)
    private Instant recebidoEm;

    protected EventoPagamento() {}

    public EventoPagamento(
            String gatewayEventoId, String tipo, String payload, Instant recebidoEm) {
        this.gatewayEventoId = gatewayEventoId;
        this.tipo = tipo;
        this.payload = payload;
        this.recebidoEm = recebidoEm;
    }

    public Long getId() {
        return id;
    }

    public String getGatewayEventoId() {
        return gatewayEventoId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getRecebidoEm() {
        return recebidoEm;
    }
}
