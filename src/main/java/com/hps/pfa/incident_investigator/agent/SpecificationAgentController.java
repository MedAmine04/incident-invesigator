package com.hps.pfa.incident_investigator.agent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpecificationAgentController {

    private final SpecificationAgentService specificationAgentService;

    public SpecificationAgentController(SpecificationAgentService specificationAgentService) {
        this.specificationAgentService = specificationAgentService;
    }

    @GetMapping("/api/spec-agent")
    public String ask(@RequestParam String message) {
        return specificationAgentService.ask(message);
    }
}