package com.thomas.nektisinvest.oferta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "oferta")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preco_centavos", nullable = false)
    private int precoCentavos;

    @Column(nullable = false, length = 3)
    private String moeda;

    @Column(name = "prazo_inscricao", nullable = false)
    private Instant prazoInscricao;

    @Column(nullable = false)
    private boolean ativo;

    protected Oferta() {}

    public Long getId() {
        return id;
    }

    public int getPrecoCentavos() {
        return precoCentavos;
    }

    public String getMoeda() {
        return moeda;
    }

    public Instant getPrazoInscricao() {
        return prazoInscricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public boolean inscricoesAbertas(Instant agora) {
        return ativo && agora.isBefore(prazoInscricao);
    }
}
