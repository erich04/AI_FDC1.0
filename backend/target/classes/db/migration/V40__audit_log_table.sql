CREATE SEQUENCE IF NOT EXISTS fdc_audit_log_t_log_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS fdc_audit_log_t (
    log_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_audit_log_t_log_id_seq'),
    object_id BIGINT NOT NULL,
    object_type VARCHAR(30) NOT NULL,
    operated_by BIGINT NOT NULL,
    opt_type VARCHAR(30) NOT NULL,
    opt_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    opt_content VARCHAR(500) NOT NULL,
    opt_status VARCHAR(30) NOT NULL,
    remark VARCHAR(500),
    enable_flag CHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP,
    sys_description VARCHAR(500),
    last_update_trace_id VARCHAR(100),
    last_update_version INT4 NOT NULL DEFAULT 0,
    tenantid BIGINT NOT NULL,
    CONSTRAINT ck_fdc_audit_log_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_audit_log_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

COMMENT ON TABLE fdc_audit_log_t IS '日志审计表';
COMMENT ON COLUMN fdc_audit_log_t.log_id IS '日志ID';
COMMENT ON COLUMN fdc_audit_log_t.object_id IS '对象ID';
COMMENT ON COLUMN fdc_audit_log_t.object_type IS '对象类型';
COMMENT ON COLUMN fdc_audit_log_t.operated_by IS '操作人（逻辑外键到 tpl_user_t.user_id）';
COMMENT ON COLUMN fdc_audit_log_t.opt_type IS '操作类型';
COMMENT ON COLUMN fdc_audit_log_t.opt_time IS '操作时间';
COMMENT ON COLUMN fdc_audit_log_t.opt_content IS '操作内容记录';
COMMENT ON COLUMN fdc_audit_log_t.opt_status IS '操作状态';
COMMENT ON COLUMN fdc_audit_log_t.remark IS '备注';

CREATE INDEX IF NOT EXISTS idx_fdc_audit_log_tn1
    ON fdc_audit_log_t (tenantid, object_type, object_id);

CREATE INDEX IF NOT EXISTS idx_fdc_audit_log_tn2
    ON fdc_audit_log_t (tenantid, operated_by, opt_time);

CREATE INDEX IF NOT EXISTS idx_fdc_audit_log_tn3
    ON fdc_audit_log_t (tenantid, opt_status, opt_time);
