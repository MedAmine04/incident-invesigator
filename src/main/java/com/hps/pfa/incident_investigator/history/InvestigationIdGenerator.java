package com.hps.pfa.incident_investigator.history;

import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * Genere des identifiants d'investigation au format professionnel : INV-MA-{annee}-{sequence sur 6 chiffres}.
 * Le compteur est remis a zero chaque annee, et la sequence est calculee a partir du nombre
 * d'investigations deja enregistrees pour l'annee en cours.
 */
@Component
public class InvestigationIdGenerator {

    private final InvestigationHistoryRepository repository;

    public InvestigationIdGenerator(InvestigationHistoryRepository repository) {
        this.repository = repository;
    }

    public String generate() {
        int currentYear = Year.now().getValue();
        long countThisYear = repository.countByGeneratedAtYear(currentYear);
        long sequence = countThisYear + 1;
        return String.format("INV-MA-%d-%06d", currentYear, sequence);
    }
}