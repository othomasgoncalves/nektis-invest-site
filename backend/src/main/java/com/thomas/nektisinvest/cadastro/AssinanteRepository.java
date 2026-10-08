package com.thomas.nektisinvest.cadastro;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssinanteRepository extends JpaRepository<Assinante, UUID> {

    Optional<Assinante> findByEmailIgnoreCase(String email);
}
