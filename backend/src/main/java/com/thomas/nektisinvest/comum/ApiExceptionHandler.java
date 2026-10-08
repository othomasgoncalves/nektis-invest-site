package com.thomas.nektisinvest.comum;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ProblemDetail tratarApiException(ApiException excecao) {
        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(excecao.situacao(), excecao.getMessage());
        problema.setTitle(excecao.titulo());
        problema.setType(URI.create("https://invest.nektis.tech/problemas/"
                + excecao.situacao().value()));
        return problema;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException excecao,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode situacao,
            @NonNull WebRequest requisicao) {

        String detalhe = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe);
        problema.setTitle("Dados inválidos");
        return handleExceptionInternal(
                excecao, problema, headers, HttpStatus.BAD_REQUEST, requisicao);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail tratarViolacaoRestricao(ConstraintViolationException excecao) {
        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, excecao.getMessage());
        problema.setTitle("Dados inválidos");
        return problema;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarInesperado(Exception excecao) {
        log.error("Erro não tratado", excecao);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro inesperado. Tente novamente em instantes.");
        problema.setTitle("Erro interno");
        return problema;
    }

    @Nullable
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception excecao,
            @Nullable Object corpo,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode codigoSituacao,
            @NonNull WebRequest requisicao) {
        return super.handleExceptionInternal(
                excecao, corpo, headers, codigoSituacao, requisicao);
    }
}
