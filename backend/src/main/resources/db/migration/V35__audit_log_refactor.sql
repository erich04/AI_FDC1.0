-- 重构文档日志为通用业务审计日志表
DROP TABLE IF EXISTS fdc_doc_op_log_t CASCADE;

CREATE TABLE IF NOT EXISTS fdc_audit_log_t (
    audit_log_id BIGSERIAL PRIMARY KEY,
    tenantid BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    object_type VARCHAR(30) NOT NULL,
    operated_by BIGINT NOT NULL,
    operation_type VARCHAR(30) NOT NULL,
    op_content VARCHAR(500) NOT NULL,
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_update_version INTEGER DEFAULT 0 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_fdc_audit_log_tn1 ON fdc_audit_log_t(tenantid);
CREATE INDEX IF NOT EXISTS idx_fdc_audit_log_n2 ON fdc_audit_log_t(object_id, object_type);
