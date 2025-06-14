CREATE TABLE purchase_transaction (
    id UUID PRIMARY KEY,
    exchange_rate FLOAT,
    converted_amount DECIMAL(19, 2),
    transaction_date DATE NOT NULL,
    purchase_amount DECIMAL(19, 2) NOT NULL,
    description VARCHAR(50)
);