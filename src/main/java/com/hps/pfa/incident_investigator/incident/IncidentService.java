package com.hps.pfa.incident_investigator.incident;

import com.hps.pfa.incident_investigator.common.exception.ResourceNotFoundException;
import com.hps.pfa.incident_investigator.contract.ClientContract;
import com.hps.pfa.incident_investigator.contract.ClientContractRepository;
import com.hps.pfa.incident_investigator.incident.dto.IncidentRequest;
import com.hps.pfa.incident_investigator.transaction.Transaction;
import com.hps.pfa.incident_investigator.transaction.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final ClientContractRepository contractRepository;
    private final TransactionRepository transactionRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           ClientContractRepository contractRepository,
                           TransactionRepository transactionRepository) {
        this.incidentRepository = incidentRepository;
        this.contractRepository = contractRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Incident create(IncidentRequest request) {
        ClientContract contract = null;
        if (request.contractId() != null) {
            contract = contractRepository.findById(request.contractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contrat introuvable avec l'id " + request.contractId()));
        }

        Incident incident = new Incident(
                request.description(),
                IncidentStatus.valueOf(request.status()),
                Instant.now(),
                contract
        );

        if (request.transactionIds() != null) {
            for (Long txId : request.transactionIds()) {
                Transaction tx = transactionRepository.findById(txId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Transaction introuvable avec l'id " + txId));
                incident.addTransaction(tx);
            }
        }

        return incidentRepository.save(incident);
    }

    public List<Incident> findAll() {
        return incidentRepository.findAllWithTransactions();
    }

    public List<Incident> findByStatus(IncidentStatus status) {
        return incidentRepository.findByStatusWithTransactions(status);
    }

    public Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident introuvable avec l'id " + id));
    }
}