package com.thomas.nektisinvest.notificacao;

import com.thomas.nektisinvest.acesso.CanalAcesso;
import com.thomas.nektisinvest.acesso.LinkAcesso;
import com.thomas.nektisinvest.cadastro.Assinante;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmailMembro {

    private final EnviadorEmail enviador;

    public EmailMembro(EnviadorEmail enviador) {
        this.enviador = enviador;
    }

    public void enviarBoasVindas(Assinante assinante, List<LinkAcesso> links) {
        StringBuilder corpo = new StringBuilder()
                .append(paragrafo("Olá, " + escapar(primeiroNome(assinante)) + "."))
                .append(paragrafo("A sua assinatura da <strong>Nektis Invest</strong> está ativa."));

        if (links.isEmpty()) {
            corpo.append(paragrafo("Os links dos canais no WhatsApp chegam para você em breve."));
        } else {
            corpo.append(paragrafo("Use os links abaixo para entrar nos dois canais:"));
            corpo.append("<ul>");
            for (LinkAcesso link : links) {
                corpo.append("<li><a href=\"").append(escapar(link.getUrl())).append("\">")
                        .append(rotulo(link.getCanal()))
                        .append("</a></li>");
            }
            corpo.append("</ul>");
        }
        corpo.append(aviso());

        enviador.enviar(
                assinante.getEmail(), "Seu acesso à Nektis Invest", envolver(corpo.toString()));
    }

    public void enviarFalhaPagamento(Assinante assinante) {
        String corpo = paragrafo("Olá, " + escapar(primeiroNome(assinante)) + ".")
                + paragrafo("Não conseguimos processar a cobrança da sua assinatura da "
                        + "<strong>Nektis Invest</strong>. Atualize os dados do cartão para "
                        + "manter o acesso aos canais.")
                + aviso();
        enviador.enviar(assinante.getEmail(), "Problema com a sua cobrança", envolver(corpo));
    }

    public void enviarCancelamento(Assinante assinante) {
        String corpo = paragrafo("Olá, " + escapar(primeiroNome(assinante)) + ".")
                + paragrafo("A sua assinatura da <strong>Nektis Invest</strong> foi cancelada e "
                        + "o acesso aos canais será encerrado. Se quiser voltar, é só assinar "
                        + "novamente pelo site.")
                + aviso();
        enviador.enviar(assinante.getEmail(), "Assinatura cancelada", envolver(corpo));
    }

    private static String rotulo(CanalAcesso canal) {
        return canal == CanalAcesso.NOTICIAS
                ? "Canal de notícias diárias"
                : "Canal de networking";
    }

    private static String primeiroNome(Assinante assinante) {
        String nome = assinante.getNome().trim();
        int espaco = nome.indexOf(' ');
        return espaco > 0 ? nome.substring(0, espaco) : nome;
    }

    private static String paragrafo(String html) {
        return "<p style=\"margin:0 0 16px;font-size:15px;line-height:1.6\">" + html + "</p>";
    }

    private static String aviso() {
        return "<p style=\"margin:28px 0 0;font-size:12px;line-height:1.6;color:#6b5a78\">"
                + "O conteúdo da Nektis Invest tem caráter informativo e educacional. "
                + "Investimentos envolvem riscos, incluindo perda do capital."
                + "</p>";
    }

    private static String envolver(String corpo) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;color:#3e1c59;"
                + "max-width:560px;margin:0 auto;padding:24px\">" + corpo + "</div>";
    }

    private static String escapar(String valor) {
        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
