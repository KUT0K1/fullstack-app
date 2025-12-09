-- Add participant_id column to payments table
ALTER TABLE payments
    ADD COLUMN participant_id BIGINT REFERENCES participants(id) ON DELETE SET NULL;

-- Make payer_name nullable (for backward compatibility, existing payments keep their payer_name)
ALTER TABLE payments
    ALTER COLUMN payer_name DROP NOT NULL;

-- Create index for better performance
CREATE INDEX idx_payments_participant_id ON payments(participant_id);

