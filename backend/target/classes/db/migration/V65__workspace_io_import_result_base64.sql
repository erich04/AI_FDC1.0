-- 应归档批量导入等异步任务：结果 Excel（Base64）存库供「我的导入」下载

ALTER TABLE fdc_workspace_io_job_t
    ADD COLUMN IF NOT EXISTS result_artifact_base64 TEXT;

COMMENT ON COLUMN fdc_workspace_io_job_t.result_artifact_base64 IS '导入结果文件（如 xlsx）Base64；IMPORT_PENDING_ARCHIVE 等';
