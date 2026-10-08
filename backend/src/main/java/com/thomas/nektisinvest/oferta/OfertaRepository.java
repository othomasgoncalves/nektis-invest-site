package com.thomas.nektisinvest.oferta;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    Optional<Oferta> findFirstByAtivoTrueOrderByIdDesc();
}
