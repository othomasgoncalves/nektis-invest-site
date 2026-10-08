package com.thomas.nektisinvest.acesso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "link_acesso")
public class LinkAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, unique = true)
    private CanalAcesso canal;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private boolean ativo;

    protected LinkAcesso() {}

    public LinkAcesso(CanalAcesso canal, String url) {
        this.canal = canal;
        this.url = url;
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public CanalAcesso getCanal() {
        return canal;
    }

    public String getUrl() {
        return url;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void atualizarUrl(String url) {
        this.url = url;
        this.ativo = true;
    }
}
