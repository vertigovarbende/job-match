CREATE TABLE jm_certification
(
    id                   BIGSERIAL PRIMARY KEY,
    candidate_id         BIGINT       NOT NULL REFERENCES jm_candidate (id),
    name                 VARCHAR(255) NOT NULL,
    issuing_organization VARCHAR(255) NOT NULL,
    issue_date           DATE         NOT NULL,
    expiry_date          DATE,
    credential_id        VARCHAR(255),
    credential_url       VARCHAR(500),
    description          TEXT,
    created_by           VARCHAR(255) NOT NULL,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by           VARCHAR(255),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_jm_certification_date_range
        CHECK (expiry_date IS NULL OR expiry_date >= issue_date)
);

CREATE INDEX idx_jm_certification_candidate_id ON jm_certification (candidate_id);
