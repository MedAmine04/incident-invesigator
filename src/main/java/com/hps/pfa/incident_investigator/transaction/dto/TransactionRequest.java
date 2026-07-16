package com.hps.pfa.incident_investigator.transaction.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRequest(
        @NotBlank String transactionRef,
        @NotNull @Positive BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotBlank @Size(min = 4, max = 4) String mti,
        String responseCode,
        String merchantId,
        String acquirerBank,
        String issuerBank,
        @NotNull Instant transactionTimestamp,
        @NotBlank String status
) {}