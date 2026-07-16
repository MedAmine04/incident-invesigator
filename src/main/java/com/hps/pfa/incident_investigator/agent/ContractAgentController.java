package com.hps.pfa.incident_investigator.agent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractAgentController {

    private final ContractAgentService contractAgentService;

    public ContractAgentController(ContractAgentService contractAgentService) {
        this.contractAgentService = contractAgentService;
    }

    @GetMapping("/api/contract-agent")
    public String ask(@RequestParam String message) {
        return contractAgentService.ask(message);
    }
}