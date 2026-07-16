package com.hps.pfa.incident_investigator.datagen;

import com.hps.pfa.incident_investigator.contract.ClientContract;
import com.hps.pfa.incident_investigator.contract.ClientContractRepository;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class ContractGenerator {

    private static final List<String> BANK_NAMES = List.of(
            "Attijariwafa Bank", "Banque Populaire", "BMCE Bank", "Credit du Maroc",
            "CFG Bank", "Societe Generale Maroc", "CIH Bank", "Al Barid Bank",
            "Bank of Africa", "Credit Agricole du Maroc", "Arab Bank Maroc",
            "Citibank Maghreb", "Umnia Bank", "Bank Assafa", "Najmah Bank"
    );

    private final ClientContractRepository repository;
    private final Random random = new Random();

    public ContractGenerator(ClientContractRepository repository) {
        this.repository = repository;
    }

    public List<ClientContract> generate() {
        return BANK_NAMES.stream()
                .map(this::buildContract)
                .map(repository::save)
                .toList();
    }

    private ClientContract buildContract(String bankName) {
        int slaHours = pickSlaHours();
        BigDecimal penalty = pickPenalty(slaHours);
        String text = buildContractText(bankName, slaHours, penalty);
        return new ClientContract(bankName, text, slaHours, penalty);
    }

    private int pickSlaHours() {
        int[] options = {2, 4, 6, 8, 12, 24};
        return options[random.nextInt(options.length)];
    }

    private BigDecimal pickPenalty(int slaHours) {
        // SLA plus court = penalite plus elevee en cas de depassement
        double base = 200.0 + (24 - slaHours) * 25.0;
        return BigDecimal.valueOf(base).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private String buildContractText(String bankName, int slaHours, BigDecimal penalty) {
        return """
            Contrat de niveau de service (SLA) - %s

            Article 1 - Delai de resolution
            En cas d'incident transactionnel impactant les services de %s,
            le delai de resolution garanti est de %d heures a compter
            de la detection de l'incident.

            Article 2 - Penalites
            Tout depassement du delai de resolution entraine une penalite
            de %s MAD par heure de retard, plafonnee a 5000 MAD par incident.

            Article 3 - Perimetre
            Le present accord couvre les incidents lies au traitement des
            transactions via le switch PowerCARD, incluant les timeouts,
            les rejets non conformes et les anomalies de reconciliation.
            """.formatted(bankName, bankName, slaHours, penalty);
    }
}