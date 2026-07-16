package com.hps.pfa.incident_investigator.web;

import com.hps.pfa.incident_investigator.history.InvestigationHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HistoryViewController {

    private final InvestigationHistoryService historyService;

    public HistoryViewController(InvestigationHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history")
    public String list(Model model) {
        model.addAttribute("histories", historyService.findAll());
        return "history";
    }

    @GetMapping("/history/{investigationId}")
    public String detail(@PathVariable String investigationId, Model model) {
        model.addAttribute("report", historyService.findByInvestigationId(investigationId));
        return "history-detail";
    }
}