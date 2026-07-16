package com.hps.pfa.incident_investigator.agent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestratorController {

    private final IncidentOrchestrationService orchestrationService;

    public OrchestratorController(IncidentOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @GetMapping("/api/investigate")
    public IncidentReport investigate(@RequestParam String incident, @RequestParam String client) {
        return orchestrationService.investigate(incident, client).report();
    }
}