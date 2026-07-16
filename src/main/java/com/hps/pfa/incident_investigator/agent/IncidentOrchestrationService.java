package com.hps.pfa.incident_investigator.agent;

import com.hps.pfa.incident_investigator.history.InvestigationHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class IncidentOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(IncidentOrchestrationService.class);

    private final TechnicalAgentService technicalAgent;
    private final SpecificationAgentService specificationAgent;
    private final ContractAgentService contractAgent;
    private final ChatClient synthesisClient;
    private final InvestigationHistoryService historyService;

    public IncidentOrchestrationService(TechnicalAgentService technicalAgent,
                                        SpecificationAgentService specificationAgent,
                                        ContractAgentService contractAgent,
                                        ChatClient.Builder chatClientBuilder,
                                        InvestigationHistoryService historyService) {
        this.technicalAgent = technicalAgent;
        this.specificationAgent = specificationAgent;
        this.contractAgent = contractAgent;
        this.synthesisClient = chatClientBuilder.build();
        this.historyService = historyService;
    }

    public InvestigationResult investigate(String incidentDescription, String clientName) {
        long startTime = System.currentTimeMillis();
        log.info(">>> [ORCHESTRATOR] Demarrage investigation : '{}', client='{}'", incidentDescription, clientName);

        String technicalQuestion = "Utilise les outils disponibles pour analyser precisement cet incident : "
                + incidentDescription + ". Verifie s'il y a un pic horaire anormal pour la banque et le statut concernes. "
                + "Donne les faits chiffres precis (volumes, banque, periode).";
        String technicalFindings = technicalAgent.ask(technicalQuestion);
        log.info(">>> [ORCHESTRATOR] Reponse agent technique recue ({} caracteres)", technicalFindings.length());

        String specQuestion = "Voici une observation technique sur un incident : " + technicalFindings
                + ". Explique la signification de ce type d'incident et la cause probable, selon la documentation technique.";
        String specFindings = specificationAgent.ask(specQuestion);
        log.info(">>> [ORCHESTRATOR] Reponse agent specifications recue ({} caracteres)", specFindings.length());

        String contractQuestion = "Pour le client " + clientName
                + ", quel est le delai de resolution garanti et les penalites applicables pour ce type d'incident : "
                + incidentDescription + " ?";
        String contractFindings = contractAgent.ask(contractQuestion);
        log.info(">>> [ORCHESTRATOR] Reponse agent contractuel recue ({} caracteres)", contractFindings.length());

        IncidentReport report = buildStructuredReport(
                incidentDescription, clientName, technicalFindings, specFindings, contractFindings);

        long executionTimeMs = System.currentTimeMillis() - startTime;
        log.info(">>> [ORCHESTRATOR] Rapport structure genere (criticite={}, confiance={}%, duree={}ms)",
                report.criticality(), report.confidencePercent(), executionTimeMs);

        var historyEntry = historyService.save(
                incidentDescription, report, technicalFindings, specFindings, contractFindings, executionTimeMs);
        log.info(">>> [ORCHESTRATOR] Investigation archivee : {}", historyEntry.investigationId());

        return new InvestigationResult(technicalFindings, specFindings, contractFindings, report,
                historyEntry.investigationId());
    }

    private IncidentReport buildStructuredReport(String incidentDescription, String clientName,
                                                 String technicalFindings, String specFindings,
                                                 String contractFindings) {
        String synthesisPrompt = """
            Tu es un assistant d'investigation d'incidents pour un switch de paiement.
            Voici les analyses de trois experts specialises sur l'incident suivant : "%s" (client : %s)

            ANALYSE TECHNIQUE (donnees factuelles) :
            %s

            ANALYSE DES SPECIFICATIONS (interpretation technique) :
            %s

            ANALYSE CONTRACTUELLE (impact SLA) :
            %s

            A partir de ces trois analyses, produis un rapport structure avec :
            - client : le nom du client concerne
            - criticality : "Faible", "Moyenne" ou "Critique"
            - confidencePercent : ton niveau de confiance dans ce diagnostic, de 0 a 100
            - executiveSummary : un resume de 2 phrases maximum, pour un lecteur non technique
            - probableCause : la cause probable de l'incident, en 3 a 5 phrases
            - contractualRisk : "Faible", "Moyen" ou "Eleve"
            - operationalRisk : "Faible", "Moyen" ou "Eleve"
            - financialRisk : "Faible", "Moyen" ou "Eleve"
            - actionPlan : une liste de 3 a 5 actions recommandees, courtes et concretes

            Sois factuel et concis.
            """.formatted(incidentDescription, clientName, technicalFindings, specFindings, contractFindings);

        return synthesisClient.prompt()
                .user(synthesisPrompt)
                .call()
                .entity(IncidentReport.class);
    }
}