package org.example.backend.market;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {

    Optional<Market> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}