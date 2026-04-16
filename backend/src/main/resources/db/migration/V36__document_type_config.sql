CREATE SEQUENCE IF NOT EXISTS fdc_document_type_t_document_type_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS fdc_document_type_t (
    document_type_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_document_type_t_document_type_id_seq'),
    doc_type_code VARCHAR(60) NOT NULL,
    doc_type_description VARCHAR(500) NOT NULL,
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
    CONSTRAINT uk_fdc_document_type_t UNIQUE (tenantid, doc_type_code),
    CONSTRAINT ck_fdc_document_type_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_document_type_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_document_type_tn1
    ON fdc_document_type_t (tenantid, doc_type_code);

CREATE INDEX IF NOT EXISTS idx_fdc_document_type_tn2
    ON fdc_document_type_t (tenantid, doc_type_description, enable_flag);
