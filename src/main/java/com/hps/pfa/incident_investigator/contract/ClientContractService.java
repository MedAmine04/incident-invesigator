package com.hps.pfa.incident_investigator.contract;

import com.hps.pfa.incident_investigator.common.exception.ResourceNotFoundException;
import com.hps.pfa.incident_investigator.contract.dto.ClientContractRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClientContractService {

    private final ClientContractRepository repository;

    public ClientContractService(ClientContractRepository repository) {
        this.repository = repository;
    }

    public ClientContract create(ClientContractRequest request) {
        ClientContract contract = new ClientContract(
                request.clientName(),
                request.contractText(),
                request.slaResolutionHours(),
                request.penaltyPerHourLate()
        );
        return repository.save(contract);
    }

    public List<ClientContract> findAll() {
        return repository.findAll();
    }

    public ClientContract findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat introuvable avec l'id " + id));
    }
}