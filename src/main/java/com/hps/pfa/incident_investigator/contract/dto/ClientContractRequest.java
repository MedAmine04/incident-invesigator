package com.hps.pfa.incident_investigator.contract.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ClientContractRequest(
        @NotBlank String clientName,
        @NotBlank String contractText,
        @NotNull @Positive Integer slaResolutionHours,
        @NotNull @PositiveOrZero BigDecimal penaltyPerHourLate
) {}