package com.thomas.nektisinvest.assinatura;

import com.thomas.nektisinvest.cadastro.Assinante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assinatura")
public class Assinatura {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assinante_id", nullable = false)
    private Assinante assinante;

    @Column(nullable = false, length = 40)
    private String gateway;

    @Column(name = "gateway_cliente_id")
    private String gatewayClienteId;

    @Column(name = "gateway_assinatura_id")
    private String gatewayAssinaturaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoAssinatura situacao;

    @Column(name = "fim_periodo_atual")
    private Instant fimPeriodoAtual;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Assinatura() {}

    public Assinatura(Assinante assinante, String gateway, Instant agora) {
        this.id = UUID.randomUUID();
        this.assinante = assinante;
        this.gateway = gateway;
        this.situacao = SituacaoAssinatura.PENDENTE;
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    public UUID getId() {
        return id;
    }

    public Assinante getAssinante() {
        return assinante;
    }

    public String getGateway() {
        return gateway;
    }

    public String getGatewayClienteId() {
        return gatewayClienteId;
    }

    public String getGatewayAssinaturaId() {
        return gatewayAssinaturaId;
    }

    public SituacaoAssinatura getSituacao() {
        return situacao;
    }

    public Instant getFimPeriodoAtual() {
        return fimPeriodoAtual;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public boolean isAtiva() {
        return situacao == SituacaoAssinatura.ATIVA;
    }

    public void vincularGateway(String clienteId, String assinaturaId, Instant agora) {
        if (clienteId != null) {
            this.gatewayClienteId = clienteId;
        }
        if (assinaturaId != null) {
            this.gatewayAssinaturaId = assinaturaId;
        }
        this.atualizadoEm = agora;
    }

    public void alterarSituacao(SituacaoAssinatura situacao, Instant fimPeriodo, Instant agora) {
        this.situacao = situacao;
        if (fimPeriodo != null) {
            this.fimPeriodoAtual = fimPeriodo;
        }
        this.atualizadoEm = agora;
    }

    public void reabrir(Instant agora) {
        this.situacao = SituacaoAssinatura.PENDENTE;
        this.atualizadoEm = agora;
    }
}
