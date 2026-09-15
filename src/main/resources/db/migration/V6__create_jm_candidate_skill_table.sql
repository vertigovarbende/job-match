CREATE TABLE jm_candidate_skill
(
    id                BIGSERIAL PRIMARY KEY,
    candidate_id      BIGINT       NOT NULL,
    skill_id          BIGINT       NOT NULL,
    proficiency_level VARCHAR(50)  NOT NULL,
    created_by        VARCHAR(255) NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by        VARCHAR(255),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_jm_candidate_skill_candidate FOREIGN KEY (candidate_id) REFERENCES jm_candidate (id),
    CONSTRAINT fk_jm_candidate_skill_skill FOREIGN KEY (skill_id) REFERENCES jm_skill (id),
    CONSTRAINT uq_jm_candidate_skill_candidate_skill UNIQUE (candidate_id, skill_id)
);

CREATE INDEX idx_jm_candidate_skill_skill_id ON jm_candidate_skill (skill_id);
