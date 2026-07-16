package com.hps.pfa.incident_investigator.incident;

import com.hps.pfa.incident_investigator.contract.ClientContract;
import com.hps.pfa.incident_investigator.transaction.Transaction;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "incident")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IncidentStatus status;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private ClientContract contract;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @ManyToMany
    @JoinTable(
            name = "incident_transaction",
            joinColumns = @JoinColumn(name = "incident_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private Set<Transaction> relatedTransactions = new HashSet<>();

    protected Incident() {
    }

    public Incident(String description, IncidentStatus status, Instant detectedAt, ClientContract contract) {
        this.description = description;
        this.status = status;
        this.detectedAt = detectedAt;
        this.contract = contract;
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public IncidentStatus getStatus() { return status; }
    public Instant getDetectedAt() { return detectedAt; }
    public ClientContract getContract() { return contract; }
    public String getAiSummary() { return aiSummary; }
    public Set<Transaction> getRelatedTransactions() { return relatedTransactions; }

    public void setStatus(IncidentStatus status) { this.status = status; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public void addTransaction(Transaction t) { this.relatedTransactions.add(t); }
}