package com.hps.pfa.incident_investigator.transaction.dto;

import com.hps.pfa.incident_investigator.transaction.Transaction;
import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        String transactionRef,
        BigDecimal amount,
        String currency,
        String mti,
        String responseCode,
        String merchantId,
        String acquirerBank,
        String issuerBank,
        Instant transactionTimestamp,
        String status
) {
    public static TransactionResponse fromEntity(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getTransactionRef(), t.getAmount(), t.getCurrency(),
                t.getMti(), t.getResponseCode(), t.getMerchantId(), t.getAcquirerBank(),
                t.getIssuerBank(), t.getTransactionTimestamp(), t.getStatus().name()
        );
    }
}