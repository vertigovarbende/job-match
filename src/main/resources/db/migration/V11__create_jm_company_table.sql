CREATE TABLE jm_company
(
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    description          TEXT,
    industry             VARCHAR(255),
    website              VARCHAR(255),
    size                 VARCHAR(50),
    headquarters_city    VARCHAR(255),
    headquarters_country VARCHAR(255),
    verified             BOOLEAN      NOT NULL DEFAULT false,
    created_by           VARCHAR(255) NOT NULL,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by           VARCHAR(255),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_jm_company_headquarters_complete
        CHECK ((headquarters_country IS NULL) = (headquarters_city IS NULL))
);