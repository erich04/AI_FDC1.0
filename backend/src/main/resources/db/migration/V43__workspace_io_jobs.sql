CREATE TABLE IF NOT EXISTS fdc_workspace_io_job_t (
    job_id BIGSERIAL PRIMARY KEY,
    job_type VARCHAR(30) NOT NULL,
    data_type VARCHAR(30) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    document_type_code VARCHAR(60),
    query_config_json TEXT,
    input_file_name VARCHAR(200),
    input_total INT,
    result_total INT,
    job_status VARCHAR(20) NOT NULL,
    error_message VARCHAR(500),
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT DEFAULT 1 NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT DEFAULT 1 NOT NULL,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tenantid BIGINT DEFAULT 1 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_workspace_io_job_tenant ON fdc_workspace_io_job_t(tenantid);
CREATE INDEX IF NOT EXISTS idx_workspace_io_job_created ON fdc_workspace_io_job_t(creation_date);
CREATE INDEX IF NOT EXISTS idx_workspace_io_job_type ON fdc_workspace_io_job_t(job_type, data_type);
