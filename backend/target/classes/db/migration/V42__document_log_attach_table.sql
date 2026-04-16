CREATE SEQUENCE IF NOT EXISTS fdc_document_log_attach_t_log_att_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS fdc_document_log_attach_t (
    log_att_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_document_log_attach_t_log_att_id_seq'),
    log_id BIGINT NOT NULL,
    file_id BIGINT,
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
    CONSTRAINT ck_fdc_document_log_attach_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_document_log_attach_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

COMMENT ON TABLE fdc_document_log_attach_t IS '文档日志附件表';
COMMENT ON COLUMN fdc_document_log_attach_t.log_att_id IS '日志附件ID';
COMMENT ON COLUMN fdc_document_log_attach_t.log_id IS '日志ID';
COMMENT ON COLUMN fdc_document_log_attach_t.file_id IS '文件ID';

CREATE INDEX IF NOT EXISTS idx_fdc_document_log_attach_tn1
    ON fdc_document_log_attach_t (tenantid, log_id);
