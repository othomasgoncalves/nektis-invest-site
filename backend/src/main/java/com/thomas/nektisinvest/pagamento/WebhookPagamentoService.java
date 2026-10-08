package com.thomas.nektisinvest.pagamento;

import com.thomas.nektisinvest.acesso.LinkAcessoService;
import com.thomas.nektisinvest.assinatura.Assinatura;
import com.thomas.nektisinvest.assinatura.AssinaturaRepository;
import com.thomas.nektisinvest.assinatura.SituacaoAssinatura;
import com.thomas.nektisinvest.notificacao.EmailMembro;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookPagamentoService {

    private static final Logger log = LoggerFactory.getLogger(WebhookPagamentoService.class);

    private final GatewayPagamento gateway;
    private final EventoPagamentoRepository eventos;
    private final AssinaturaRepository assinaturas;
    private final LinkAcessoService linksAcesso;
    private final EmailMembro email;
    private final Clock relogio;

    public WebhookPagamentoService(
            GatewayPagamento gateway,
            EventoPagamentoRepository eventos,
            AssinaturaRepository assinaturas,
            LinkAcessoService linksAcesso,
            EmailMembro email,
            Clock relogio) {
        this.gateway = gateway;
        this.eventos = eventos;
        this.assinaturas = assinaturas;
        this.linksAcesso = linksAcesso;
        this.email = email;
        this.relogio = relogio;
    }

    @Transactional
    public boolean processar(String payload, String headerAssinatura) {
        Optional<EventoGateway> lido = gateway.lerWebhook(payload, headerAssinatura);
        if (lido.isEmpty()) {
            return false;
        }
        EventoGateway evento = lido.get();

        if (eventos.existsByGatewayEventoId(evento.id())) {
            log.debug("Ignorando evento duplicado do gateway {}", evento.id());
            return false;
        }
        try {
            eventos.saveAndFlush(new EventoPagamento(
                    evento.id(), evento.tipo(), payload, Instant.now(relogio)));
        } catch (DataIntegrityViolationException excecao) {
            log.debug("Evento do gateway duplicado e concorrente {}", evento.id());
            return false;
        }

        Optional<Assinatura> alvo = localizar(evento);
        if (alvo.isEmpty()) {
            log.warn("Evento do gateway {} ({}) não corresponde a nenhuma assinatura",
                    evento.id(), evento.tipo());
            return false;
        }

        Assinatura assinatura = alvo.get();
        SituacaoAssinatura anterior = assinatura.getSituacao();
        Instant agora = Instant.now(relogio);

        assinatura.vincularGateway(evento.clienteId(), evento.assinaturaId(), agora);
        assinatura.alterarSituacao(evento.situacao(), evento.fimPeriodoAtual(), agora);

        notificar(assinatura, anterior, evento.situacao());
        return true;
    }

    private Optional<Assinatura> localizar(EventoGateway evento) {
        if (evento.assinaturaLocalId() != null) {
            try {
                Optional<Assinatura> porLocalId =
                        assinaturas.findById(UUID.fromString(evento.assinaturaLocalId()));
                if (porLocalId.isPresent()) {
                    return porLocalId;
                }
            } catch (IllegalArgumentException ignorada) {
                log.debug("Metadados com id fora do nosso formato: {}",
                        evento.assinaturaLocalId());
            }
        }
        if (evento.assinaturaId() != null) {
            Optional<Assinatura> porAssinatura =
                    assinaturas.findByGatewayAssinaturaId(evento.assinaturaId());
            if (porAssinatura.isPresent()) {
                return porAssinatura;
            }
        }
        if (evento.clienteId() != null) {
            return assinaturas.findFirstByGatewayClienteIdOrderByCriadoEmDesc(evento.clienteId());
        }
        return Optional.empty();
    }

    private void notificar(
            Assinatura assinatura,
            SituacaoAssinatura anterior,
            SituacaoAssinatura atual) {

        if (anterior == atual) {
            return;
        }
        switch (atual) {
            case ATIVA -> email.enviarBoasVindas(
                    assinatura.getAssinante(), linksAcesso.linksAtivos());
            case INADIMPLENTE -> email.enviarFalhaPagamento(assinatura.getAssinante());
            case CANCELADA -> email.enviarCancelamento(assinatura.getAssinante());
            case PENDENTE -> log.debug("Assinatura {} voltou para PENDENTE", assinatura.getId());
        }
    }
}
