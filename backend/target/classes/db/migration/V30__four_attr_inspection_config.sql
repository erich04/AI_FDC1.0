CREATE SEQUENCE IF NOT EXISTS fdc_four_attr_inspection_t_inspection_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS fdc_four_attr_inspection_t (
    inspection_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_four_attr_inspection_t_inspection_id_seq'),
    inspection_name VARCHAR(100) NOT NULL,
    inspection_stage VARCHAR(30) NOT NULL,
    data_package_spec VARCHAR(100) NOT NULL,
    metadata_spec VARCHAR(100) NOT NULL,
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
    CONSTRAINT uk_fdc_four_attr_inspection_t UNIQUE (tenantid, inspection_name, inspection_stage),
    CONSTRAINT ck_fdc_four_attr_inspection_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_four_attr_inspection_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_four_attr_inspection_tn1
    ON fdc_four_attr_inspection_t (tenantid, inspection_name);
CREATE INDEX IF NOT EXISTS idx_fdc_four_attr_inspection_tn2
    ON fdc_four_attr_inspection_t (tenantid, inspection_stage, enable_flag);

CREATE SEQUENCE IF NOT EXISTS fdc_four_attr_inspection_detail_t_detail_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS fdc_four_attr_inspection_detail_t (
    detail_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_four_attr_inspection_detail_t_detail_id_seq'),
    inspection_id BIGINT NOT NULL,
    inspection_type VARCHAR(30) NOT NULL,
    inspection_code VARCHAR(60),
    inspection_item VARCHAR(500),
    inspection_purpose VARCHAR(500),
    inspection_object VARCHAR(500),
    inspection_basis_method VARCHAR(1000),
    display_order INT4 NOT NULL DEFAULT 1,
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
    CONSTRAINT ck_fdc_four_attr_inspection_detail_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_four_attr_inspection_detail_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_four_attr_inspection_detail_tn1
    ON fdc_four_attr_inspection_detail_t (tenantid, inspection_id, inspection_type, display_order);
