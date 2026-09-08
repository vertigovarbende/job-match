CREATE TABLE jm_audit_log
(
    id             BIGSERIAL PRIMARY KEY,
    occurred_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    actor_id       VARCHAR(255),
    actor_role     VARCHAR(100),
    action         VARCHAR(100) NOT NULL,
    target_type    VARCHAR(100) NOT NULL,
    target_id      VARCHAR(255) NOT NULL,
    correlation_id VARCHAR(255),
    details        JSONB
);

CREATE INDEX idx_jm_audit_log_target ON jm_audit_log (target_type, target_id);
CREATE INDEX idx_jm_audit_log_occurred_at ON jm_audit_log (occurred_at);
CREATE INDEX idx_jm_audit_log_actor_id ON jm_audit_log (actor_id);
