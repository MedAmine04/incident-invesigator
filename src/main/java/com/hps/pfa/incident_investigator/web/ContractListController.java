package com.hps.pfa.incident_investigator.web;

import com.hps.pfa.incident_investigator.contract.ClientContractRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContractListController {

    private final ClientContractRepository contractRepository;

    public ContractListController(ClientContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @GetMapping("/contracts")
    public String list(Model model) {
        model.addAttribute("contracts", contractRepository.findAll());
        return "contracts";
    }
}