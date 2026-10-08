package com.thomas.nektisinvest.pagamento;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoPagamentoRepository extends JpaRepository<EventoPagamento, Long> {

    boolean existsByGatewayEventoId(String gatewayEventoId);
}
