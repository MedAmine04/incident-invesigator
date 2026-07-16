package com.hps.pfa.incident_investigator.contract;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "client_contract")
public class ClientContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "contract_text", nullable = false, columnDefinition = "TEXT")
    private String contractText;

    @Column(name = "sla_resolution_hours", nullable = false)
    private Integer slaResolutionHours;

    @Column(name = "penalty_per_hour_late", nullable = false, precision = 12, scale = 2)
    private BigDecimal penaltyPerHourLate;

    protected ClientContract() {
        // constructeur vide requis par JPA
    }

    public ClientContract(String clientName, String contractText,
                          Integer slaResolutionHours, BigDecimal penaltyPerHourLate) {
        this.clientName = clientName;
        this.contractText = contractText;
        this.slaResolutionHours = slaResolutionHours;
        this.penaltyPerHourLate = penaltyPerHourLate;
    }

    public Long getId() { return id; }
    public String getClientName() { return clientName; }
    public String getContractText() { return contractText; }
    public Integer getSlaResolutionHours() { return slaResolutionHours; }
    public BigDecimal getPenaltyPerHourLate() { return penaltyPerHourLate; }

    public void setClientName(String clientName) { this.clientName = clientName; }
    public void setContractText(String contractText) { this.contractText = contractText; }
    public void setSlaResolutionHours(Integer h) { this.slaResolutionHours = h; }
    public void setPenaltyPerHourLate(BigDecimal p) { this.penaltyPerHourLate = p; }
}