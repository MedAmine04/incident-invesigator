package com.hps.pfa.incident_investigator.history.dto;

import com.hps.pfa.incident_investigator.history.InvestigationHistory;

import java.time.Instant;
import java.util.List;

public record InvestigationHistoryResponse(
        Long id,
        String investigationId,
        String client,
        String incidentDescription,
        String executiveSummary,
        String probableCause,
        String technicalFindings,
        String specificationFindings,
        String contractFindings,
        String contractualRisk,
        String operationalRisk,
        String financialRisk,
        List<String> recommendedActions,
        String criticality,
        int confidence,
        Instant generatedAt,
        long executionTimeMs
) {
    public static InvestigationHistoryResponse fromEntity(InvestigationHistory h) {
        return new InvestigationHistoryResponse(
                h.getId(), h.getInvestigationId(), h.getClient(), h.getIncidentDescription(),
                h.getExecutiveSummary(), h.getProbableCause(),
                h.getTechnicalFindings(), h.getSpecificationFindings(), h.getContractFindings(),
                h.getContractualRisk(), h.getOperationalRisk(), h.getFinancialRisk(),
                h.getRecommendedActions(), h.getCriticality(), h.getConfidence(),
                h.getGeneratedAt(), h.getExecutionTimeMs()
        );
    }
}