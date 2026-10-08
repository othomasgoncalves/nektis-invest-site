package com.thomas.nektisinvest.cadastro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assinante")
public class Assinante {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(name = "hash_token_acesso", nullable = false, length = 64)
    private String hashTokenAcesso;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Assinante() {}

    public Assinante(
            String nome,
            String email,
            String telefone,
            String hashTokenAcesso,
            Instant criadoEm) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.hashTokenAcesso = hashTokenAcesso;
        this.criadoEm = criadoEm;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getHashTokenAcesso() {
        return hashTokenAcesso;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void atualizar(String nome, String telefone, String hashTokenAcesso) {
        this.nome = nome;
        this.telefone = telefone;
        this.hashTokenAcesso = hashTokenAcesso;
    }
}
