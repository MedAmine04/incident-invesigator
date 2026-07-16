package com.hps.pfa.incident_investigator.transaction;

import com.hps.pfa.incident_investigator.transaction.dto.TransactionRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction create(TransactionRequest request) {
        Transaction transaction = new Transaction(
                request.transactionRef(),
                request.amount(),
                request.currency(),
                request.mti(),
                request.responseCode(),
                request.merchantId(),
                request.acquirerBank(),
                request.issuerBank(),
                request.transactionTimestamp(),
                TransactionStatus.valueOf(request.status())
        );
        return repository.save(transaction);
    }

    public List<Transaction> findAll() {
        return repository.findAll();
    }

    public Transaction findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new com.hps.pfa.incident_investigator.common.exception.ResourceNotFoundException(
                        "Transaction introuvable avec l'id " + id));
    }
}