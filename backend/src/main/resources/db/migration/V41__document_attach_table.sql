CREATE SEQUENCE IF NOT EXISTS fdc_document_attach_t_attachment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS fdc_document_attach_t (
    attachment_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_document_attach_t_attachment_id_seq'),
    doc_id BIGINT NOT NULL,
    file_id BIGINT,
    attachment_category VARCHAR(30),
    attachment_classification VARCHAR(30),
    attachment_description VARCHAR(500),
    source_system VARCHAR(30),
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
    CONSTRAINT ck_fdc_document_attach_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_document_attach_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

DO $$
DECLARE
    i INTEGER;
BEGIN
    FOR i IN 1..100 LOOP
        EXECUTE format(
            'ALTER TABLE fdc_document_attach_t ADD COLUMN IF NOT EXISTS attr%s VARCHAR(500)',
            i
        );
    END LOOP;
END $$;

COMMENT ON TABLE fdc_document_attach_t IS '文档附件表';
COMMENT ON COLUMN fdc_document_attach_t.attachment_id IS '附件ID';
COMMENT ON COLUMN fdc_document_attach_t.doc_id IS '文档ID';
COMMENT ON COLUMN fdc_document_attach_t.file_id IS '文件ID';
COMMENT ON COLUMN fdc_document_attach_t.attachment_category IS '附件类别';
COMMENT ON COLUMN fdc_document_attach_t.attachment_classification IS '附件类型';
COMMENT ON COLUMN fdc_document_attach_t.attachment_description IS '描述';
COMMENT ON COLUMN fdc_document_attach_t.source_system IS '业务系统标识';

CREATE INDEX IF NOT EXISTS idx_fdc_document_attach_tn1
    ON fdc_document_attach_t (tenantid, doc_id);

CREATE INDEX IF NOT EXISTS idx_fdc_document_attach_tn2
    ON fdc_document_attach_t (tenantid, attachment_category, attachment_classification);
