package com.hps.pfa.incident_investigator.transaction;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_ref", nullable = false, unique = true, length = 64)
    private String transactionRef;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 4)
    private String mti;

    @Column(name = "response_code", length = 4)
    private String responseCode;

    @Column(name = "merchant_id", length = 64)
    private String merchantId;

    @Column(name = "acquirer_bank", length = 128)
    private String acquirerBank;

    @Column(name = "issuer_bank", length = 128)
    private String issuerBank;

    @Column(name = "transaction_timestamp", nullable = false)
    private Instant transactionTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransactionStatus status;

    protected Transaction() {
    }

    public Transaction(String transactionRef, BigDecimal amount, String currency, String mti,
                       String responseCode, String merchantId, String acquirerBank,
                       String issuerBank, Instant transactionTimestamp, TransactionStatus status) {
        this.transactionRef = transactionRef;
        this.amount = amount;
        this.currency = currency;
        this.mti = mti;
        this.responseCode = responseCode;
        this.merchantId = merchantId;
        this.acquirerBank = acquirerBank;
        this.issuerBank = issuerBank;
        this.transactionTimestamp = transactionTimestamp;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getTransactionRef() { return transactionRef; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getMti() { return mti; }
    public String getResponseCode() { return responseCode; }
    public String getMerchantId() { return merchantId; }
    public String getAcquirerBank() { return acquirerBank; }
    public String getIssuerBank() { return issuerBank; }
    public Instant getTransactionTimestamp() { return transactionTimestamp; }
    public TransactionStatus getStatus() { return status; }
}