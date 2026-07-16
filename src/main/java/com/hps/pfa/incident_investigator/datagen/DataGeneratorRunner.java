package com.hps.pfa.incident_investigator.datagen;

import com.hps.pfa.incident_investigator.contract.ClientContract;
import com.hps.pfa.incident_investigator.contract.ClientContractRepository;
import com.hps.pfa.incident_investigator.transaction.Transaction;
import com.hps.pfa.incident_investigator.transaction.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataGeneratorRunner implements CommandLineRunner {

    private final ClientContractRepository contractRepository;
    private final TransactionRepository transactionRepository;
    private final ContractGenerator contractGenerator;
    private final TransactionGenerator transactionGenerator;
    private final IncidentGenerator incidentGenerator;

    public DataGeneratorRunner(ClientContractRepository contractRepository,
                               TransactionRepository transactionRepository,
                               ContractGenerator contractGenerator,
                               TransactionGenerator transactionGenerator,
                               IncidentGenerator incidentGenerator) {
        this.contractRepository = contractRepository;
        this.transactionRepository = transactionRepository;
        this.contractGenerator = contractGenerator;
        this.transactionGenerator = transactionGenerator;
        this.incidentGenerator = incidentGenerator;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (transactionRepository.count() > 0) {
            System.out.println(">>> Donnees deja presentes, generation ignoree.");
            return;
        }

        System.out.println(">>> Generation des donnees synthetiques...");

        List<ClientContract> contracts = contractGenerator.generate();
        System.out.println(">>> " + contracts.size() + " contrats generes.");

        List<Transaction> transactions = transactionGenerator.generate(5000, 30);
        System.out.println(">>> " + transactions.size() + " transactions generees.");

        List<?> incidents = incidentGenerator.generate(transactions, contracts, 75);
        System.out.println(">>> " + incidents.size() + " incidents generes.");

        System.out.println(">>> Generation terminee.");
    }
}