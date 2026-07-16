package com.hps.pfa.incident_investigator.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientContractRepository extends JpaRepository<ClientContract, Long> {

    Optional<ClientContract> findByClientName(String clientName);
}