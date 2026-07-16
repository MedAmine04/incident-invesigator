package com.hps.pfa.incident_investigator.contract.dto;

import com.hps.pfa.incident_investigator.contract.ClientContract;
import java.math.BigDecimal;

public record ClientContractResponse(
        Long id,
        String clientName,
        String contractText,
        Integer slaResolutionHours,
        BigDecimal penaltyPerHourLate
) {
    public static ClientContractResponse fromEntity(ClientContract c) {
        return new ClientContractResponse(
                c.getId(), c.getClientName(), c.getContractText(),
                c.getSlaResolutionHours(), c.getPenaltyPerHourLate()
        );
    }
}