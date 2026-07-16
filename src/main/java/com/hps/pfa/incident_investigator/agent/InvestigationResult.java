package com.hps.pfa.incident_investigator.agent;

public record InvestigationResult(
        String technicalFindings,
        String specificationFindings,
        String contractFindings,
        IncidentReport report,
        String investigationId
) {}