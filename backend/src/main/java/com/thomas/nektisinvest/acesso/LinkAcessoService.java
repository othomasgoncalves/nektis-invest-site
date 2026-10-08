package com.thomas.nektisinvest.acesso;

import com.thomas.nektisinvest.config.NektisProperties;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkAcessoService {

    private static final Logger log = LoggerFactory.getLogger(LinkAcessoService.class);

    private final LinkAcessoRepository repository;
    private final NektisProperties properties;

    public LinkAcessoService(LinkAcessoRepository repository, NektisProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void sincronizarComAmbiente() {
        Map<CanalAcesso, String> configurados = Map.of(
                CanalAcesso.NOTICIAS, nuloParaVazio(properties.linksAcesso().noticias()),
                CanalAcesso.NETWORKING, nuloParaVazio(properties.linksAcesso().networking()));

        configurados.forEach((canal, url) -> {
            if (url.isBlank()) {
                repository.findByCanal(canal).ifPresent(repository::delete);
                return;
            }
            repository.findByCanal(canal)
                    .ifPresentOrElse(
                            existente -> existente.atualizarUrl(url),
                            () -> repository.save(new LinkAcesso(canal, url)));
        });

        long total = repository.findAllByAtivoTrue().size();
        if (total == 0) {
            log.warn("Nenhum link de acesso do WhatsApp configurado — os membros ativos serão "
                    + "avisados de que os links chegam por e-mail.");
        }
    }

    @Transactional(readOnly = true)
    public List<LinkAcesso> linksAtivos() {
        List<LinkAcesso> links = new ArrayList<>(repository.findAllByAtivoTrue());
        links.sort(Comparator.comparing(LinkAcesso::getCanal));
        return links;
    }

    private static String nuloParaVazio(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
