package com.hps.pfa.incident_investigator.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvestigationHistoryRepository extends JpaRepository<InvestigationHistory, Long> {

    Optional<InvestigationHistory> findByInvestigationId(String investigationId);

    List<InvestigationHistory> findAllByOrderByGeneratedAtDesc();

    @Query("SELECT COUNT(h) FROM InvestigationHistory h WHERE EXTRACT(YEAR FROM h.generatedAt) = :year")
    long countByGeneratedAtYear(@Param("year") int year);
}