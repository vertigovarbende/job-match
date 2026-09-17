CREATE TABLE jm_job
(
    id                 BIGSERIAL PRIMARY KEY,
    company_id         BIGINT       NOT NULL,
    title              VARCHAR(255) NOT NULL,
    description        TEXT,
    seniority          VARCHAR(50),
    employment_type    VARCHAR(50),
    workplace_type     VARCHAR(50),
    location_country   VARCHAR(255),
    location_city      VARCHAR(255),
    salary_min_amount  NUMERIC(19, 2),
    salary_max_amount  NUMERIC(19, 2),
    salary_currency    VARCHAR(3),
    minimum_experience INTEGER,
    status             VARCHAR(50)  NOT NULL DEFAULT 'DRAFT',
    published_at       TIMESTAMPTZ,
    expires_at         TIMESTAMPTZ,
    created_by         VARCHAR(255) NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by         VARCHAR(255),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_jm_job_company FOREIGN KEY (company_id) REFERENCES jm_company (id),

    CONSTRAINT chk_jm_job_location_complete
        CHECK ((location_country IS NULL) = (location_city IS NULL)),

    CONSTRAINT chk_jm_job_salary_complete
        CHECK (
            (salary_min_amount IS NULL AND salary_max_amount IS NULL AND salary_currency IS NULL)
                OR
            (salary_min_amount IS NOT NULL AND salary_max_amount IS NOT NULL AND salary_currency IS NOT NULL)
            ),

    CONSTRAINT chk_jm_job_salary_range
        CHECK (
            salary_min_amount IS NULL
                OR salary_max_amount IS NULL
                OR salary_min_amount <= salary_max_amount
            )
);

CREATE INDEX idx_jm_job_company_id ON jm_job (company_id);
