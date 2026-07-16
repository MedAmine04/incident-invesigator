package com.hps.pfa.incident_investigator.contract;

import com.hps.pfa.incident_investigator.contract.dto.ClientContractRequest;
import com.hps.pfa.incident_investigator.contract.dto.ClientContractResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ClientContractController {

    private final ClientContractService service;

    public ClientContractController(ClientContractService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientContractResponse create(@Valid @RequestBody ClientContractRequest request) {
        return ClientContractResponse.fromEntity(service.create(request));
    }

    @GetMapping
    public List<ClientContractResponse> findAll() {
        return service.findAll().stream()
                .map(ClientContractResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ClientContractResponse findById(@PathVariable Long id) {
        return ClientContractResponse.fromEntity(service.findById(id));
    }
}