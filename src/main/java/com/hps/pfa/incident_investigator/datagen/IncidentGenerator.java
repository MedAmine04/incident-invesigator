package com.hps.pfa.incident_investigator.datagen;

import com.hps.pfa.incident_investigator.contract.ClientContract;
import com.hps.pfa.incident_investigator.incident.Incident;
import com.hps.pfa.incident_investigator.incident.IncidentRepository;
import com.hps.pfa.incident_investigator.incident.IncidentStatus;
import com.hps.pfa.incident_investigator.transaction.Transaction;
import com.hps.pfa.incident_investigator.transaction.TransactionStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class IncidentGenerator {

    private static final List<String> DESCRIPTION_TEMPLATES = List.of(
            "Hausse anormale de timeouts detectee sur les transactions de %s",
            "Augmentation des rejets avec code 51 (provision insuffisante) chez %s",
            "Panne suspectee cote emetteur %s - transactions en erreur 91",
            "Probleme de reconciliation signale pour les transactions %s",
            "Pic de latence reseau impactant le traitement des transactions %s"
    );

    private final IncidentRepository incidentRepository;
    private final Random random = new Random();

    public IncidentGenerator(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<Incident> generate(List<Transaction> allTransactions,
                                   List<ClientContract> contracts,
                                   int count) {
        List<Transaction> problematic = allTransactions.stream()
                .filter(t -> t.getStatus() != TransactionStatus.APPROVED)
                .collect(Collectors.toList());

        if (problematic.isEmpty()) {
            return List.of();
        }

        List<Incident> incidents = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            incidents.add(buildOne(problematic, contracts));
        }
        return incidentRepository.saveAll(incidents);
    }

    private Incident buildOne(List<Transaction> problematic, List<ClientContract> contracts) {
        // Selectionne une banque emettrice "incriminee" a partir d'une transaction problematique
        Transaction seed = problematic.get(random.nextInt(problematic.size()));
        String bankName = seed.getIssuerBank();

        ClientContract relatedContract = contracts.stream()
                .filter(c -> c.getClientName().equals(bankName))
                .findFirst()
                .orElse(contracts.get(random.nextInt(contracts.size())));

        String template = DESCRIPTION_TEMPLATES.get(random.nextInt(DESCRIPTION_TEMPLATES.size()));
        String description = template.formatted(bankName);

        IncidentStatus status = pickStatus();
        Instant detectedAt = seed.getTransactionTimestamp().plusSeconds(60 + random.nextInt(600));

        Incident incident = new Incident(description, status, detectedAt, relatedContract);

        // Lie 2 a 6 transactions problematiques de la meme banque a cet incident
        List<Transaction> sameBank = problematic.stream()
                .filter(t -> t.getIssuerBank().equals(bankName))
                .limit(2 + random.nextInt(5))
                .toList();
        sameBank.forEach(incident::addTransaction);

        return incident;
    }

    private IncidentStatus pickStatus() {
        double roll = random.nextDouble();
        if (roll < 0.3) return IncidentStatus.OPEN;
        if (roll < 0.5) return IncidentStatus.INVESTIGATING;
        return IncidentStatus.RESOLVED;
    }
}