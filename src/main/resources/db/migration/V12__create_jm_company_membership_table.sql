CREATE TABLE jm_company_membership
(
    id         BIGSERIAL PRIMARY KEY,
    company_id BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by VARCHAR(255),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_jm_company_membership_company FOREIGN KEY (company_id) REFERENCES jm_company (id),
    CONSTRAINT fk_jm_company_membership_user FOREIGN KEY (user_id) REFERENCES jm_user (id),
    CONSTRAINT uq_jm_company_membership_user UNIQUE (user_id)
);

CREATE INDEX idx_jm_company_membership_company_id ON jm_company_membership (company_id);
