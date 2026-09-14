CREATE TABLE jm_candidate
(
    id                         BIGSERIAL PRIMARY KEY,
    user_id                    BIGINT       NOT NULL UNIQUE REFERENCES jm_user (id),
    headline                   VARCHAR(255),
    summary                    TEXT,
    location_country           VARCHAR(255),
    location_city              VARCHAR(255),
    desired_salary_min_amount  NUMERIC(19, 2),
    desired_salary_max_amount  NUMERIC(19, 2),
    desired_salary_currency    VARCHAR(3),
    open_to_on_site            BOOLEAN,
    open_to_remote             BOOLEAN,
    open_to_hybrid             BOOLEAN,
    created_by                 VARCHAR(255) NOT NULL,
    created_at                 TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by                 VARCHAR(255),
    updated_at                 TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_jm_candidate_location_complete
        CHECK ((location_country IS NULL) = (location_city IS NULL)),

    CONSTRAINT chk_jm_candidate_desired_salary_complete
        CHECK (
            (desired_salary_min_amount IS NULL AND desired_salary_max_amount IS NULL AND desired_salary_currency IS NULL)
                OR
            (desired_salary_min_amount IS NOT NULL AND desired_salary_max_amount IS NOT NULL AND desired_salary_currency IS NOT NULL)
            ),

    CONSTRAINT chk_jm_candidate_desired_salary_range
        CHECK (
            desired_salary_min_amount IS NULL
                OR desired_salary_max_amount IS NULL
                OR desired_salary_min_amount <= desired_salary_max_amount
            ),

    CONSTRAINT chk_jm_candidate_workplace_preferences_complete
        CHECK (
            (open_to_on_site IS NULL AND open_to_remote IS NULL AND open_to_hybrid IS NULL)
                OR
            (open_to_on_site IS NOT NULL AND open_to_remote IS NOT NULL AND open_to_hybrid IS NOT NULL)
            )
);