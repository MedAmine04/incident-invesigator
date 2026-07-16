package com.hps.pfa.incident_investigator.agent;

import java.util.List;

public record IncidentReport(
        String client,
        String criticality,
        int confidencePercent,
        String executiveSummary,
        String probableCause,
        String contractualRisk,
        String operationalRisk,
        String financialRisk,
        List<String> actionPlan
) {}