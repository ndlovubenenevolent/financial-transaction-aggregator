CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    source VARCHAR(20) NOT NULL,
    category VARCHAR(30) NOT NULL,
    description VARCHAR(500) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    transaction_date TIMESTAMPTZ NOT NULL,
    external_transaction_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_source_external_id UNIQUE (source, external_transaction_id)
);

CREATE INDEX idx_transactions_customer_id ON transactions(customer_id);
CREATE INDEX idx_transactions_category ON transactions(category);
CREATE INDEX idx_transactions_source ON transactions(source);
CREATE INDEX idx_transactions_transaction_date ON transactions(transaction_date);
