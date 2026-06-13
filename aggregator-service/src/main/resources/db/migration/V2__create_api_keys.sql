CREATE TABLE api_keys (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    api_key_hash VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_used_at TIMESTAMP
);

CREATE INDEX idx_api_keys_active ON api_keys(active);

-- demo-api-key (BCrypt hash; plain key documented in README)
INSERT INTO api_keys (client_name, api_key_hash, active, created_at)
VALUES (
    'demo-client',
    '$2b$10$Q/tiSVMwHg/Bmwx6MPm/E.Hw9jhjQQ4xUBms9VNBAoreO/lZzPbAe',
    TRUE,
    NOW()
);
