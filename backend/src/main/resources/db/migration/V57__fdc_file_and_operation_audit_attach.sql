-- 文件元数据表（若历史库未执行 V33 或表缺失则补齐）
CREATE TABLE IF NOT EXISTS fdc_file_t (
    file_id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(50),
    source_system VARCHAR(60),
    storage_platform VARCHAR(60) NOT NULL,
    file_md5 VARCHAR(64),
    enable_flag CHAR(1) DEFAULT 'Y' NOT NULL,
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_fdc_file_t_delete ON fdc_file_t(delete_flag);

-- 操作审计补充说明附件：关联 fdc_operation_audit_log_t 与 fdc_file_t
CREATE TABLE IF NOT EXISTS dc_operation_audit_log_attach_t (
    attach_id BIGSERIAL PRIMARY KEY,
    tenantid BIGINT NOT NULL DEFAULT 1,
    audit_log_id BIGINT NOT NULL,
    file_id BIGINT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dc_op_audit_att_log FOREIGN KEY (audit_log_id) REFERENCES fdc_operation_audit_log_t(audit_log_id) ON DELETE CASCADE,
    CONSTRAINT fk_dc_op_audit_att_file FOREIGN KEY (file_id) REFERENCES fdc_file_t(file_id)
);

CREATE INDEX IF NOT EXISTS idx_dc_op_audit_att_log ON dc_operation_audit_log_attach_t(audit_log_id);
CREATE INDEX IF NOT EXISTS idx_dc_op_audit_att_file ON dc_operation_audit_log_attach_t(file_id);
