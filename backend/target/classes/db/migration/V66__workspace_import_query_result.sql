CREATE TABLE IF NOT EXISTS fdc_workspace_import_query_result_t (
    result_id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL,
    query_row_no INT4 NOT NULL,
    archive_id BIGINT,
    doc_id VARCHAR(64),
    business_code VARCHAR(200),
    document_name VARCHAR(500),
    doc_status VARCHAR(100),
    lifecycle_status VARCHAR(100),
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tenantid BIGINT NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_fdc_workspace_import_query_result_job
    ON fdc_workspace_import_query_result_t(job_id);

CREATE INDEX IF NOT EXISTS idx_fdc_workspace_import_query_result_created
    ON fdc_workspace_import_query_result_t(creation_date);
