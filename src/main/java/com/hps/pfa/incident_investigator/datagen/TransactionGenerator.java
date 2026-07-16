package com.hps.pfa.incident_investigator.datagen;

import com.hps.pfa.incident_investigator.transaction.Transaction;
import com.hps.pfa.incident_investigator.transaction.TransactionRepository;
import com.hps.pfa.incident_investigator.transaction.TransactionStatus;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TransactionGenerator {

    private static final List<String> ISSUER_BANKS = List.of(
            "Attijariwafa Bank", "Banque Populaire", "BMCE Bank", "Credit du Maroc",
            "CFG Bank", "Societe Generale Maroc", "CIH Bank", "Al Barid Bank",
            "Bank of Africa", "Credit Agricole du Maroc"
    );
    private static final List<String> ACQUIRER_BANKS = List.of(
            "Attijariwafa Bank", "Banque Populaire", "BMCE Bank", "CIH Bank"
    );
    private static final List<String> MTI_CODES = List.of("0100", "0200", "0220", "0400");
    private static final List<String> APPROVED_CODES = List.of("00");
    private static final List<String> DECLINED_CODES = List.of("05", "14", "51", "61");
    private static final List<String> ERROR_CODES = List.of("96", "91", "30");

    private final TransactionRepository repository;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    public TransactionGenerator(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> generate(int count, int overDays) {
        List<Transaction> generated = new ArrayList<>();
        // Pre-calcul d'une banque "a probleme" et d'une fenetre horaire "a probleme"
        String troubledBank = ISSUER_BANKS.get(random.nextInt(ISSUER_BANKS.size()));
        int troubledHourStart = 14; // pic d'incidents entre 14h et 15h, comme souhaite

        for (int i = 0; i < count; i++) {
            generated.add(buildOne(i, overDays, troubledBank, troubledHourStart));
        }
        return repository.saveAll(generated);
    }

    private Transaction buildOne(int index, int overDays, String troubledBank, int troubledHour) {
        String issuerBank = ISSUER_BANKS.get(random.nextInt(ISSUER_BANKS.size()));
        String acquirerBank = ACQUIRER_BANKS.get(random.nextInt(ACQUIRER_BANKS.size()));
        String merchantId = "MERCH-" + String.format("%04d", random.nextInt(200));
        String mti = MTI_CODES.get(random.nextInt(MTI_CODES.size()));

        ZonedDateTime timestamp = randomTimestamp(overDays);
        boolean isTroubledWindow = issuerBank.equals(troubledBank)
                && timestamp.getHour() == troubledHour;

        TransactionStatus status = pickStatus(isTroubledWindow);
        String responseCode = pickResponseCode(status);
        BigDecimal amount = pickAmount(status);

        String ref = "TX-" + String.format("%06d", index + 1);

        return new Transaction(
                ref, amount, "MAD", mti, responseCode, merchantId,
                acquirerBank, issuerBank, timestamp.toInstant(), status
        );
    }

    private TransactionStatus pickStatus(boolean isTroubledWindow) {
        double roll = random.nextDouble();
        if (isTroubledWindow) {
            // Dans la fenetre a probleme : beaucoup plus de timeouts
            if (roll < 0.55) return TransactionStatus.TIMEOUT;
            if (roll < 0.75) return TransactionStatus.DECLINED;
            if (roll < 0.85) return TransactionStatus.ERROR;
            return TransactionStatus.APPROVED;
        }
        // Distribution normale : ~92% approuve, 5% refuse, 2% timeout, 1% erreur
        if (roll < 0.92) return TransactionStatus.APPROVED;
        if (roll < 0.97) return TransactionStatus.DECLINED;
        if (roll < 0.99) return TransactionStatus.TIMEOUT;
        return TransactionStatus.ERROR;
    }

    private String pickResponseCode(TransactionStatus status) {
        return switch (status) {
            case APPROVED -> APPROVED_CODES.get(0);
            case DECLINED -> DECLINED_CODES.get(random.nextInt(DECLINED_CODES.size()));
            case ERROR -> ERROR_CODES.get(random.nextInt(ERROR_CODES.size()));
            case TIMEOUT -> null; // un timeout n'a souvent pas de code reponse, comme en reel
        };
    }

    private BigDecimal pickAmount(TransactionStatus status) {
        double base = 20 + random.nextDouble() * 4000;
        // bruit volontaire : quelques montants aberrants, rares
        if (random.nextDouble() < 0.01) {
            base *= 20; // pic anormal occasionnel
        }
        return BigDecimal.valueOf(base).setScale(2, RoundingMode.HALF_UP);
    }

    private ZonedDateTime randomTimestamp(int overDays) {
        long secondsRange = overDays * 24L * 3600L;
        long offsetSeconds = (long) (random.nextDouble() * secondsRange);
        return ZonedDateTime.now(ZoneOffset.UTC)
                .minusSeconds(offsetSeconds);
    }
}