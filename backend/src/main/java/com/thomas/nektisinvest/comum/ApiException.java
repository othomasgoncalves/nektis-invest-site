package com.thomas.nektisinvest.comum;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus situacao;
    private final String titulo;

    public ApiException(HttpStatus situacao, String titulo, String detalhe) {
        super(detalhe);
        this.situacao = situacao;
        this.titulo = titulo;
    }

    public HttpStatus situacao() {
        return situacao;
    }

    public String titulo() {
        return titulo;
    }
}
