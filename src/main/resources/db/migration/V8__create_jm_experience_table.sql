CREATE TABLE jm_experience
(
    id               BIGSERIAL PRIMARY KEY,
    candidate_id     BIGINT       NOT NULL REFERENCES jm_candidate (id),
    title            VARCHAR(255) NOT NULL,
    company          VARCHAR(255) NOT NULL,
    location_country VARCHAR(255),
    location_city    VARCHAR(255),
    employment_type  VARCHAR(50),
    start_date       DATE         NOT NULL,
    end_date         DATE,
    description      TEXT,
    created_by       VARCHAR(255) NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by       VARCHAR(255),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_jm_experience_location_complete
        CHECK ((location_country IS NULL) = (location_city IS NULL)),

    CONSTRAINT chk_jm_experience_date_range
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX idx_jm_experience_candidate_id ON jm_experience (candidate_id);
