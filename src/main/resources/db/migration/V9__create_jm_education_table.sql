CREATE TABLE jm_education
(
    id             BIGSERIAL PRIMARY KEY,
    candidate_id   BIGINT       NOT NULL REFERENCES jm_candidate (id),
    institution    VARCHAR(255) NOT NULL,
    degree         VARCHAR(255),
    field_of_study VARCHAR(255),
    start_date     DATE         NOT NULL,
    end_date       DATE,
    description    TEXT,
    created_by     VARCHAR(255) NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by     VARCHAR(255),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_jm_education_date_range
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX idx_jm_education_candidate_id ON jm_education (candidate_id);
