package com.hps.pfa.incident_investigator.incident.dto;

import com.hps.pfa.incident_investigator.incident.Incident;
import java.time.Instant;
import java.util.List;

public record IncidentResponse(
        Long id,
        String description,
        String status,
        Instant detectedAt,
        Long contractId,
        String aiSummary,
        List<Long> transactionIds
) {
    public static IncidentResponse fromEntity(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getDescription(),
                incident.getStatus().name(),
                incident.getDetectedAt(),
                incident.getContract() != null ? incident.getContract().getId() : null,
                incident.getAiSummary(),
                incident.getRelatedTransactions().stream()
                        .map(t -> t.getId())
                        .toList()
        );
    }
}