CREATE TABLE jm_candidate_language
(
    id                BIGSERIAL PRIMARY KEY,
    candidate_id      BIGINT       NOT NULL,
    language_id       BIGINT       NOT NULL,
    proficiency_level VARCHAR(50)  NOT NULL,
    created_by        VARCHAR(255) NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by        VARCHAR(255),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_jm_candidate_language_candidate FOREIGN KEY (candidate_id) REFERENCES jm_candidate (id),
    CONSTRAINT fk_jm_candidate_language_language FOREIGN KEY (language_id) REFERENCES jm_language (id),
    CONSTRAINT uq_jm_candidate_language_candidate_language UNIQUE (candidate_id, language_id)
);

CREATE INDEX idx_jm_candidate_language_language_id ON jm_candidate_language (language_id);
