-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE job (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    provider_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    wage DECIMAL(10, 2),

    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,

    -- Storing array of strings for skills
    required_skills TEXT[],

    status VARCHAR(50) DEFAULT 'OPEN',
    source VARCHAR(50),
    number_of_employees INTEGER DEFAULT 1,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Index for searching jobs by a specific provider
CREATE INDEX idx_job_provider_id ON job(provider_id);