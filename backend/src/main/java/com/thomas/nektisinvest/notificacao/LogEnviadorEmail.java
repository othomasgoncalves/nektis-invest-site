package com.thomas.nektisinvest.notificacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "nektis.email.provedor", havingValue = "log")
public class LogEnviadorEmail implements EnviadorEmail {

    private static final Logger log = LoggerFactory.getLogger(LogEnviadorEmail.class);

    @Override
    public void enviar(String para, String assunto, String html) {
        log.info("""
                [e-mail suprimido]
                  para:    {}
                  assunto: {}
                  corpo:
                {}""", para, assunto, html);
    }
}
