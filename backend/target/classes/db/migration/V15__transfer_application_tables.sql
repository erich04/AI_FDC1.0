CREATE SEQUENCE IF NOT EXISTS fdc_application_t_application_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS fdc_application_t (
    application_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_application_t_application_id_seq'),
    application_number VARCHAR(60) NOT NULL,
    applicant BIGINT,
    application_date TIMESTAMP,
    department VARCHAR(60),
    document_type_code VARCHAR(30),
    apply_method VARCHAR(30),
    express_type VARCHAR(30),
    express_number VARCHAR(60),
    document_recipient BIGINT,
    handover_form VARCHAR(30),
    carrier_type VARCHAR(30),
    application_description VARCHAR(500),
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
    CONSTRAINT uk_fdc_application_t UNIQUE (tenantid, application_number),
    CONSTRAINT ck_fdc_application_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_application_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_application_tn1
    ON fdc_application_t (tenantid, application_date);
CREATE INDEX IF NOT EXISTS idx_fdc_application_tn2
    ON fdc_application_t (tenantid, document_type_code);

CREATE SEQUENCE IF NOT EXISTS fdc_application_detail_t_application_detail_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS fdc_application_detail_t (
    application_detail_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_application_detail_t_application_detail_id_seq'),
    application_id BIGINT NOT NULL,
    doc_busi_no VARCHAR(60),
    doc_name VARCHAR(500),
    busi_module_code VARCHAR(30),
    arch_place_alpha2_code VARCHAR(100),
    end_arch_period DATE,
    start_arch_period DATE,
    arch_type_code VARCHAR(30),
    doc_generation_date DATE,
    arch_copies DECIMAL(24, 10),
    remark VARCHAR(500),
    description VARCHAR(500),
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
    CONSTRAINT ck_fdc_application_detail_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_application_detail_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_application_detail_tn1
    ON fdc_application_detail_t (tenantid, application_id);
CREATE INDEX IF NOT EXISTS idx_fdc_application_detail_tn2
    ON fdc_application_detail_t (tenantid, doc_busi_no);

CREATE SEQUENCE IF NOT EXISTS fdc_application_ext_t_ext_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS fdc_application_ext_t (
    ext_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_application_ext_t_ext_id_seq'),
    object_id BIGINT NOT NULL,
    master_id BIGINT NOT NULL,
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
    CONSTRAINT ck_fdc_application_ext_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_application_ext_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_application_ext_tn1
    ON fdc_application_ext_t (tenantid, object_id, master_id);

DO $$
DECLARE
    i INTEGER;
BEGIN
    FOR i IN 1..100 LOOP
        EXECUTE format(
            'ALTER TABLE fdc_application_ext_t ADD COLUMN IF NOT EXISTS attr%s VARCHAR(500)',
            i
        );
    END LOOP;

    FOR i IN 101..200 LOOP
        EXECUTE format(
            'ALTER TABLE fdc_application_ext_t ADD COLUMN IF NOT EXISTS attr%s DECIMAL(38,10)',
            i
        );
    END LOOP;

    FOR i IN 201..300 LOOP
        EXECUTE format(
            'ALTER TABLE fdc_application_ext_t ADD COLUMN IF NOT EXISTS attr%s DATE',
            i
        );
    END LOOP;
END $$;
