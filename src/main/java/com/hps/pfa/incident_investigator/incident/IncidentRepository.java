package com.hps.pfa.incident_investigator.incident;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    @Query("SELECT DISTINCT i FROM Incident i LEFT JOIN FETCH i.relatedTransactions WHERE i.status = :status")
    List<Incident> findByStatusWithTransactions(IncidentStatus status);

    @Query("SELECT DISTINCT i FROM Incident i LEFT JOIN FETCH i.relatedTransactions")
    List<Incident> findAllWithTransactions();

    @Query("SELECT DISTINCT i FROM Incident i LEFT JOIN FETCH i.contract LEFT JOIN FETCH i.relatedTransactions")
    List<Incident> findAllWithDetails();

    @Query("SELECT i.status, COUNT(i) FROM Incident i GROUP BY i.status")
    List<Object[]> countByStatus();
}