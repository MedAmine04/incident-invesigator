-- V1: Schema initial - transactions, incidents, contrats clients

CREATE TABLE client_contract (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL,
    contract_text TEXT NOT NULL,
    sla_resolution_hours INTEGER NOT NULL,
    penalty_per_hour_late NUMERIC(12, 2) NOT NULL
);

CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    transaction_ref VARCHAR(64) NOT NULL UNIQUE,
    amount NUMERIC(14, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    mti VARCHAR(4) NOT NULL,
    response_code VARCHAR(4),
    merchant_id VARCHAR(64),
    acquirer_bank VARCHAR(128),
    issuer_bank VARCHAR(128),
    transaction_timestamp TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE incident (
    id BIGSERIAL PRIMARY KEY,
    description TEXT,
    status VARCHAR(32) NOT NULL,
    detected_at TIMESTAMP NOT NULL,
    contract_id BIGINT REFERENCES client_contract(id),
    ai_summary TEXT
);

CREATE TABLE incident_transaction (
    incident_id BIGINT NOT NULL REFERENCES incident(id) ON DELETE CASCADE,
    transaction_id BIGINT NOT NULL REFERENCES transaction(id) ON DELETE CASCADE,
    PRIMARY KEY (incident_id, transaction_id)
);

CREATE INDEX idx_transaction_timestamp ON transaction(transaction_timestamp);
CREATE INDEX idx_transaction_status ON transaction(status);
CREATE INDEX idx_incident_status ON incident(status);