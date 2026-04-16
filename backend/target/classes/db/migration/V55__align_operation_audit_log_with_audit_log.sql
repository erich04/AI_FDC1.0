-- Align fdc_operation_audit_log_t column layout with fdc_audit_log_t (V35).
-- Module / business key / snapshots live in op_content JSON for query + API compatibility.

ALTER TABLE fdc_audit_log_t
    ALTER COLUMN op_content TYPE TEXT USING op_content::TEXT;

ALTER TABLE fdc_operation_audit_log_t RENAME TO fdc_operation_audit_log_t_old;

CREATE TABLE fdc_operation_audit_log_t (
    audit_log_id BIGSERIAL PRIMARY KEY,
    tenantid BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    object_type VARCHAR(30) NOT NULL,
    operated_by BIGINT NOT NULL,
    operation_type VARCHAR(30) NOT NULL,
    op_content TEXT NOT NULL,
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_update_version INTEGER DEFAULT 0 NOT NULL
);

CREATE INDEX idx_fdc_operation_audit_tn1 ON fdc_operation_audit_log_t (tenantid);
CREATE INDEX idx_fdc_operation_audit_n2 ON fdc_operation_audit_log_t (object_id, object_type);

INSERT INTO fdc_operation_audit_log_t (
    tenantid,
    object_id,
    object_type,
    operated_by,
    operation_type,
    op_content,
    operation_time,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    last_update_version
)
SELECT
    tenantid,
    CASE
        WHEN business_key ~ '^[0-9]+$' THEN business_key::BIGINT
        ELSE 0
    END,
    LEFT(module_code, 30),
    operator_id,
    LEFT(operation_type, 30),
    json_build_object(
        'moduleName', module_name,
        'businessType', business_type,
        'businessKey', business_key,
        'summary', operation_summary,
        'before', before_snapshot,
        'after', after_snapshot,
        'operatorName', operator_name
    )::text,
    operation_time,
    operator_id,
    operation_time,
    NULL,
    operation_time,
    COALESCE(last_update_version, 0)
FROM fdc_operation_audit_log_t_old;

DROP TABLE fdc_operation_audit_log_t_old;
