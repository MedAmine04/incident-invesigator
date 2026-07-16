package com.hps.pfa.incident_investigator.agent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestChatController {

    private final TechnicalAgentService technicalAgentService;

    public TestChatController(TechnicalAgentService technicalAgentService) {
        this.technicalAgentService = technicalAgentService;
    }

    @GetMapping("/api/test-ai")
    public String testChat(@RequestParam(defaultValue = "Dis bonjour en une phrase") String message) {
        return technicalAgentService.ask(message);
    }
}