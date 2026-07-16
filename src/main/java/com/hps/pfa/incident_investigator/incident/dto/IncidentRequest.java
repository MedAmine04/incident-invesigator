package com.hps.pfa.incident_investigator.incident.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record IncidentRequest(
        String description,
        @NotBlank String status,
        Long contractId,
        List<Long> transactionIds
) {}