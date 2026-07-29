CREATE TABLE incidents (

    id UUID PRIMARY KEY,

    title VARCHAR(255) NOT NULL,

    description TEXT,

    severity VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMP,

    updated_at TIMESTAMP

);