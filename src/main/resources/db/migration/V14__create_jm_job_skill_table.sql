CREATE TABLE jm_job_skill
(
    id         BIGSERIAL PRIMARY KEY,
    job_id     BIGINT       NOT NULL,
    skill_id   BIGINT       NOT NULL,
    skill_type VARCHAR(50)  NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by VARCHAR(255),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_jm_job_skill_job FOREIGN KEY (job_id) REFERENCES jm_job (id),
    CONSTRAINT fk_jm_job_skill_skill FOREIGN KEY (skill_id) REFERENCES jm_skill (id),
    CONSTRAINT uq_jm_job_skill_job_skill UNIQUE (job_id, skill_id)
);

CREATE INDEX idx_jm_job_skill_skill_id ON jm_job_skill (skill_id);
