-- Drop existing example table if exists
DROP TABLE IF EXISTS example;

-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create events table
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    adult_budget DECIMAL(10, 2) NOT NULL,
    child_budget DECIMAL(10, 2) NOT NULL,
    general_costs DECIMAL(10, 2) DEFAULT 0.00,
    creator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create participants table
CREATE TABLE participants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('ADULT', 'CHILD')),
    custom_budget DECIMAL(10, 2),
    is_couple BOOLEAN NOT NULL DEFAULT FALSE,
    partner_id BIGINT REFERENCES participants(id) ON DELETE SET NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE
);

-- Create payments table
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payer_name VARCHAR(255) NOT NULL,
    note TEXT,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_events_creator_id ON events(creator_id);
CREATE INDEX idx_participants_event_id ON participants(event_id);
CREATE INDEX idx_participants_user_id ON participants(user_id);
CREATE INDEX idx_payments_event_id ON payments(event_id);

