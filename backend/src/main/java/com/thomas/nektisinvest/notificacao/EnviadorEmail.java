package com.thomas.nektisinvest.notificacao;

public interface EnviadorEmail {

    void enviar(String para, String assunto, String html);
}
