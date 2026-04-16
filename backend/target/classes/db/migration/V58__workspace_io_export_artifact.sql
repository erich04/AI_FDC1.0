-- Server-side export artifact retention (download within 7 days); import failed CSV cleanup is enforced in application logic.
ALTER TABLE fdc_workspace_io_job_t
    ADD COLUMN IF NOT EXISTS export_file_format VARCHAR(20),
    ADD COLUMN IF NOT EXISTS result_artifact_text TEXT,
    ADD COLUMN IF NOT EXISTS artifact_expires_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_workspace_io_job_creator ON fdc_workspace_io_job_t(created_by, creation_date);
