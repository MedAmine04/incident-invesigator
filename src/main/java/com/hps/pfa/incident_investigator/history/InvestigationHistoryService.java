package com.hps.pfa.incident_investigator.history;

import com.hps.pfa.incident_investigator.agent.IncidentReport;
import com.hps.pfa.incident_investigator.common.exception.ResourceNotFoundException;
import com.hps.pfa.incident_investigator.history.dto.InvestigationHistoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvestigationHistoryService {

    private final InvestigationHistoryRepository repository;
    private final InvestigationIdGenerator idGenerator;

    public InvestigationHistoryService(InvestigationHistoryRepository repository,
                                       InvestigationIdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public InvestigationHistoryResponse save(String incidentDescription, IncidentReport report,
                                             String technicalFindings, String specificationFindings,
                                             String contractFindings, long executionTimeMs) {
        String investigationId = idGenerator.generate();

        InvestigationHistory history = new InvestigationHistory(
                investigationId,
                report.client(),
                incidentDescription,
                report.executiveSummary(),
                report.probableCause(),
                technicalFindings,
                specificationFindings,
                contractFindings,
                report.contractualRisk(),
                report.operationalRisk(),
                report.financialRisk(),
                report.actionPlan(),
                report.criticality(),
                report.confidencePercent(),
                Instant.now(),
                executionTimeMs
        );

        return InvestigationHistoryResponse.fromEntity(repository.save(history));
    }

    public List<InvestigationHistoryResponse> findAll() {
        return repository.findAllByOrderByGeneratedAtDesc().stream()
                .map(InvestigationHistoryResponse::fromEntity)
                .toList();
    }

    public InvestigationHistoryResponse findByInvestigationId(String investigationId) {
        return repository.findByInvestigationId(investigationId)
                .map(InvestigationHistoryResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Investigation introuvable : " + investigationId));
    }
}