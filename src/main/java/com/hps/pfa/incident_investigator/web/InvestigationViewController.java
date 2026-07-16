package com.hps.pfa.incident_investigator.web;

import com.hps.pfa.incident_investigator.agent.IncidentOrchestrationService;
import com.hps.pfa.incident_investigator.agent.InvestigationResult;
import com.hps.pfa.incident_investigator.contract.ClientContractRepository;
import com.hps.pfa.incident_investigator.incident.IncidentRepository;
import com.hps.pfa.incident_investigator.transaction.TransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InvestigationViewController {

    private final IncidentOrchestrationService orchestrationService;
    private final TransactionRepository transactionRepository;
    private final IncidentRepository incidentRepository;
    private final ClientContractRepository contractRepository;
    private final DashboardChartService chartService;

    public InvestigationViewController(IncidentOrchestrationService orchestrationService,
                                       TransactionRepository transactionRepository,
                                       IncidentRepository incidentRepository,
                                       ClientContractRepository contractRepository,
                                       DashboardChartService chartService) {
        this.orchestrationService = orchestrationService;
        this.transactionRepository = transactionRepository;
        this.incidentRepository = incidentRepository;
        this.contractRepository = contractRepository;
        this.chartService = chartService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        addKpis(model);
        model.addAttribute("transactionDonut", chartService.transactionStatusDonut());
        model.addAttribute("incidentDonut", chartService.incidentStatusDonut());
        model.addAttribute("topBanks", chartService.topBanksByTimeout());
        model.addAttribute("hourlyBars", chartService.timeoutHourlyDistribution());
        return "dashboard";
    }

    @GetMapping("/investigation")
    public String investigationForm() {
        return "investigation";
    }

    @PostMapping("/investigate")
    public String investigate(@RequestParam String incident,
                              @RequestParam String client,
                              Model model) {
        InvestigationResult result = orchestrationService.investigate(incident, client);
        model.addAttribute("investigationId", result.investigationId());
        model.addAttribute("incident", incident);
        model.addAttribute("client", client);
        model.addAttribute("result", result);
        model.addAttribute("submitted", true);
        return "investigation";
    }

    private void addKpis(Model model) {
        model.addAttribute("transactionCount", transactionRepository.count());
        model.addAttribute("incidentCount", incidentRepository.count());
        model.addAttribute("contractCount", contractRepository.count());
    }
}