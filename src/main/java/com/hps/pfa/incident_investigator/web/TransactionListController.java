package com.hps.pfa.incident_investigator.web;

import com.hps.pfa.incident_investigator.transaction.Transaction;
import com.hps.pfa.incident_investigator.transaction.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TransactionListController {

    private static final int PAGE_SIZE = 25;

    private final TransactionRepository transactionRepository;

    public TransactionListController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/transactions")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by("transactionTimestamp").descending());
        Page<Transaction> result = transactionRepository.findAll(pageRequest);

        model.addAttribute("transactions", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalCount", result.getTotalElements());
        return "transactions";
    }
}