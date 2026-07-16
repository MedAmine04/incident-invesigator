package com.hps.pfa.incident_investigator.agent;

import com.hps.pfa.incident_investigator.transaction.TransactionRepository;
import com.hps.pfa.incident_investigator.transaction.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class TransactionTools {

    private static final Logger log = LoggerFactory.getLogger(TransactionTools.class);

    private final TransactionRepository transactionRepository;

    public TransactionTools(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(description = "Compte le nombre de transactions pour une banque emettrice donnee, "
            + "avec un statut donne (APPROVED, DECLINED, TIMEOUT, ERROR), "
            + "sur les N derniers jours.")
    public long countTransactionsByBankAndStatus(
            @ToolParam(description = "Nom exact de la banque emettrice, ex: 'Attijariwafa Bank'") String issuerBank,
            @ToolParam(description = "Statut de la transaction: APPROVED, DECLINED, TIMEOUT ou ERROR") String status,
            @ToolParam(description = "Nombre de jours en arriere a considerer depuis maintenant") int lastDays
    ) {
        log.info(">>> [TOOL CALL] countTransactionsByBankAndStatus(issuerBank='{}', status='{}', lastDays={})",
                issuerBank, status, lastDays);

        TransactionStatus statusEnum = TransactionStatus.valueOf(status.toUpperCase());
        Instant from = Instant.now().minus(lastDays, ChronoUnit.DAYS);
        Instant to = Instant.now();

        long result = transactionRepository
                .findByIssuerBankAndStatus(issuerBank, statusEnum)
                .stream()
                .filter(t -> !t.getTransactionTimestamp().isBefore(from)
                        && !t.getTransactionTimestamp().isAfter(to))
                .count();

        log.info(">>> [TOOL RESULT] {} transactions trouvees", result);

        return result;
    }
    @Tool(description = "Liste les references et timestamps des transactions recentes "
            + "pour une banque emettrice et un statut donnes, limitees a un nombre maximum de resultats. "
            + "Utile pour examiner le detail d'un probleme, pas juste le compter.")
    public String listRecentTransactions(
            @ToolParam(description = "Nom exact de la banque emettrice") String issuerBank,
            @ToolParam(description = "Statut: APPROVED, DECLINED, TIMEOUT ou ERROR") String status,
            @ToolParam(description = "Nombre maximum de transactions a retourner") int maxResults
    ) {
        log.info(">>> [TOOL CALL] listRecentTransactions(issuerBank='{}', status='{}', maxResults={})",
                issuerBank, status, maxResults);

        TransactionStatus statusEnum = TransactionStatus.valueOf(status.toUpperCase());

        String result = transactionRepository
                .findByIssuerBankAndStatus(issuerBank, statusEnum)
                .stream()
                .sorted((a, b) -> b.getTransactionTimestamp().compareTo(a.getTransactionTimestamp()))
                .limit(maxResults)
                .map(t -> t.getTransactionRef() + " à " + t.getTransactionTimestamp())
                .reduce("", (a, b) -> a + b + "\n");

        log.info(">>> [TOOL RESULT] {} lignes retournees", maxResults);
        return result.isEmpty() ? "Aucune transaction trouvee." : result;
    }

    @Tool(description = "Calcule, pour une banque emettrice et un statut donnes, "
            + "la repartition du nombre de transactions par heure de la journee (0 a 23). "
            + "Permet de detecter un pic horaire anormal.")
    public String getHourlyDistribution(
            @ToolParam(description = "Nom exact de la banque emettrice") String issuerBank,
            @ToolParam(description = "Statut: APPROVED, DECLINED, TIMEOUT ou ERROR") String status
    ) {
        log.info(">>> [TOOL CALL] getHourlyDistribution(issuerBank='{}', status='{}')", issuerBank, status);

        TransactionStatus statusEnum = TransactionStatus.valueOf(status.toUpperCase());

        java.util.Map<Integer, Long> distribution = transactionRepository
                .findByIssuerBankAndStatus(issuerBank, statusEnum)
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        t -> t.getTransactionTimestamp().atZone(java.time.ZoneOffset.UTC).getHour(),
                        java.util.stream.Collectors.counting()
                ));

        StringBuilder sb = new StringBuilder();
        distribution.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(e -> sb.append(String.format("%02dh: %d transactions\n", e.getKey(), e.getValue())));

        log.info(">>> [TOOL RESULT] distribution sur {} heures distinctes", distribution.size());
        return sb.toString();
    }

    @Tool(description = "Compare plusieurs banques emettrices entre elles sur un statut donne, "
            + "et retourne le nombre de transactions pour chacune, trie du plus eleve au plus faible.")
    public String compareIssuerBanks(
            @ToolParam(description = "Liste des noms de banques a comparer, separes par des virgules") String bankNamesCsv,
            @ToolParam(description = "Statut: APPROVED, DECLINED, TIMEOUT ou ERROR") String status
    ) {
        log.info(">>> [TOOL CALL] compareIssuerBanks(banks='{}', status='{}')", bankNamesCsv, status);

        TransactionStatus statusEnum = TransactionStatus.valueOf(status.toUpperCase());
        String[] bankNames = bankNamesCsv.split(",");

        StringBuilder sb = new StringBuilder();
        java.util.Arrays.stream(bankNames)
                .map(String::trim)
                .map(bank -> new Object[]{
                        bank,
                        transactionRepository.findByIssuerBankAndStatus(bank, statusEnum).size()
                })
                .sorted((a, b) -> ((Integer) b[1]).compareTo((Integer) a[1]))
                .forEach(arr -> sb.append(arr[0]).append(": ").append(arr[1]).append(" transactions\n"));

        log.info(">>> [TOOL RESULT] comparaison de {} banques effectuee", bankNames.length);
        return sb.toString();
    }
    @Tool(description = "Liste tous les noms distincts de banques emettrices presentes dans les donnees. "
            + "Utile avant de comparer plusieurs banques entre elles si l'utilisateur ne precise pas de liste.")
    public String listAllIssuerBanks() {
        log.info(">>> [TOOL CALL] listAllIssuerBanks()");

        String result = transactionRepository.findAll().stream()
                .map(t -> t.getIssuerBank())
                .distinct()
                .sorted()
                .reduce("", (a, b) -> a + b + "\n");

        log.info(">>> [TOOL RESULT] liste des banques retournee");
        return result;
    }

    @Tool(description = "Identifie, parmi toutes les banques emettrices, celle qui a le plus grand nombre "
            + "de transactions avec un statut donne. Repond directement a des questions comme "
            + "'quelle banque est la plus impactee par les timeouts'.")
    public String findMostImpactedBank(
            @ToolParam(description = "Statut a analyser: APPROVED, DECLINED, TIMEOUT ou ERROR") String status
    ) {
        log.info(">>> [TOOL CALL] findMostImpactedBank(status='{}')", status);

        TransactionStatus statusEnum = TransactionStatus.valueOf(status.toUpperCase());

        java.util.Optional<java.util.Map.Entry<String, Long>> top = transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == statusEnum)
                .collect(java.util.stream.Collectors.groupingBy(
                        com.hps.pfa.incident_investigator.transaction.Transaction::getIssuerBank,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue());

        String result = top
                .map(e -> e.getKey() + " avec " + e.getValue() + " transactions en statut " + status)
                .orElse("Aucune transaction trouvee pour ce statut.");

        log.info(">>> [TOOL RESULT] {}", result);
        return result;
    }
}