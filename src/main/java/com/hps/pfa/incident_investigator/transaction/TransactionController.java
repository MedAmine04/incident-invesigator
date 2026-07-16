package com.hps.pfa.incident_investigator.transaction;

import com.hps.pfa.incident_investigator.transaction.dto.TransactionRequest;
import com.hps.pfa.incident_investigator.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionRequest request) {
        return TransactionResponse.fromEntity(service.create(request));
    }

    @GetMapping
    public List<TransactionResponse> findAll() {
        return service.findAll().stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable Long id) {
        return TransactionResponse.fromEntity(service.findById(id));
    }
}