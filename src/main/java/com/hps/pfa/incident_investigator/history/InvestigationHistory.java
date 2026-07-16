package com.hps.pfa.incident_investigator.history;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "investigation_history")
public class InvestigationHistory {

    private static final String ACTIONS_SEPARATOR = "\u241F"; // separateur invisible, jamais present dans du texte naturel

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investigation_id", nullable = false, unique = true, length = 32)
    private String investigationId;

    @Column(nullable = false)
    private String client;

    @Column(name = "incident_description", nullable = false, columnDefinition = "TEXT")
    private String incidentDescription;

    @Column(name = "executive_summary", columnDefinition = "TEXT")
    private String executiveSummary;

    @Column(name = "probable_cause", columnDefinition = "TEXT")
    private String probableCause;

    @Column(name = "technical_findings", columnDefinition = "TEXT")
    private String technicalFindings;

    @Column(name = "specification_findings", columnDefinition = "TEXT")
    private String specificationFindings;

    @Column(name = "contract_findings", columnDefinition = "TEXT")
    private String contractFindings;

    @Column(name = "contractual_risk", length = 32)
    private String contractualRisk;

    @Column(name = "operational_risk", length = 32)
    private String operationalRisk;

    @Column(name = "financial_risk", length = 32)
    private String financialRisk;

    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    private String recommendedActionsRaw;

    @Column(nullable = false, length = 32)
    private String criticality;

    @Column(nullable = false)
    private int confidence;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @Column(name = "execution_time_ms", nullable = false)
    private long executionTimeMs;

    protected InvestigationHistory() {
        // requis par JPA
    }

    public InvestigationHistory(String investigationId, String client, String incidentDescription,
                                String executiveSummary, String probableCause,
                                String technicalFindings, String specificationFindings, String contractFindings,
                                String contractualRisk, String operationalRisk, String financialRisk,
                                List<String> recommendedActions, String criticality, int confidence,
                                Instant generatedAt, long executionTimeMs) {
        this.investigationId = investigationId;
        this.client = client;
        this.incidentDescription = incidentDescription;
        this.executiveSummary = executiveSummary;
        this.probableCause = probableCause;
        this.technicalFindings = technicalFindings;
        this.specificationFindings = specificationFindings;
        this.contractFindings = contractFindings;
        this.contractualRisk = contractualRisk;
        this.operationalRisk = operationalRisk;
        this.financialRisk = financialRisk;
        this.recommendedActionsRaw = String.join(ACTIONS_SEPARATOR, recommendedActions);
        this.criticality = criticality;
        this.confidence = confidence;
        this.generatedAt = generatedAt;
        this.executionTimeMs = executionTimeMs;
    }

    public List<String> getRecommendedActions() {
        return recommendedActionsRaw == null || recommendedActionsRaw.isBlank()
                ? List.of()
                : List.of(recommendedActionsRaw.split(ACTIONS_SEPARATOR));
    }

    public Long getId() { return id; }
    public String getInvestigationId() { return investigationId; }
    public String getClient() { return client; }
    public String getIncidentDescription() { return incidentDescription; }
    public String getExecutiveSummary() { return executiveSummary; }
    public String getProbableCause() { return probableCause; }
    public String getTechnicalFindings() { return technicalFindings; }
    public String getSpecificationFindings() { return specificationFindings; }
    public String getContractFindings() { return contractFindings; }
    public String getContractualRisk() { return contractualRisk; }
    public String getOperationalRisk() { return operationalRisk; }
    public String getFinancialRisk() { return financialRisk; }
    public String getCriticality() { return criticality; }
    public int getConfidence() { return confidence; }
    public Instant getGeneratedAt() { return generatedAt; }
    public long getExecutionTimeMs() { return executionTimeMs; }
}