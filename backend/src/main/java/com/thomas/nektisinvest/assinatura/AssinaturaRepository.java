package com.thomas.nektisinvest.assinatura;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssinaturaRepository extends JpaRepository<Assinatura, UUID> {

    Optional<Assinatura> findFirstByAssinanteIdOrderByCriadoEmDesc(UUID assinanteId);

    Optional<Assinatura> findByGatewayAssinaturaId(String gatewayAssinaturaId);

    Optional<Assinatura> findFirstByGatewayClienteIdOrderByCriadoEmDesc(String gatewayClienteId);

    boolean existsByAssinanteIdAndSituacao(UUID assinanteId, SituacaoAssinatura situacao);
}
