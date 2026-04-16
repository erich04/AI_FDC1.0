ALTER TABLE fdc_workspace_io_job_t
    ADD COLUMN IF NOT EXISTS duration_ms BIGINT;

ALTER TABLE fdc_workspace_io_job_t
    ADD COLUMN IF NOT EXISTS failed_file_csv TEXT;

CREATE INDEX IF NOT EXISTS idx_workspace_io_job_status ON fdc_workspace_io_job_t(job_status);
