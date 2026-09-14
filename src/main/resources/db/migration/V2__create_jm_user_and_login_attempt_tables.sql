CREATE TABLE jm_user
(
    id                      BIGSERIAL PRIMARY KEY,
    keycloak_subject_id     UUID         NOT NULL,
    first_name              VARCHAR(255),
    last_name               VARCHAR(255),
    email                   VARCHAR(255) NOT NULL,
    role                    VARCHAR(50)  NOT NULL,
    created_by              VARCHAR(255) NOT NULL,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by              VARCHAR(255),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_jm_user_keycloak_subject_id ON jm_user (keycloak_subject_id);

CREATE TABLE jm_user_login_attempt
(
    id              BIGSERIAL PRIMARY KEY,
    last_login_at   TIMESTAMPTZ,
    user_id         BIGINT       NOT NULL UNIQUE REFERENCES jm_user (id),
    created_by      VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);