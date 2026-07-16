-- V2: Historique des investigations multi-agents

CREATE TABLE investigation_history (
    id BIGSERIAL PRIMARY KEY,
    investigation_id VARCHAR(32) NOT NULL UNIQUE,
    client VARCHAR(255) NOT NULL,
    incident_description TEXT NOT NULL,
    executive_summary TEXT,
    probable_cause TEXT,
    technical_findings TEXT,
    specification_findings TEXT,
    contract_findings TEXT,
    contractual_risk VARCHAR(32),
    operational_risk VARCHAR(32),
    financial_risk VARCHAR(32),
    recommended_actions TEXT,
    criticality VARCHAR(32) NOT NULL,
    confidence INTEGER NOT NULL,
    generated_at TIMESTAMP NOT NULL,
    execution_time_ms BIGINT NOT NULL
);

CREATE INDEX idx_history_generated_at ON investigation_history(generated_at);
CREATE INDEX idx_history_client ON investigation_history(client);
CREATE INDEX idx_history_investigation_id ON investigation_history(investigation_id);