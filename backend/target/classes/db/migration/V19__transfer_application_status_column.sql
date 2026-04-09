ALTER TABLE fdc_application_t ADD COLUMN IF NOT EXISTS status VARCHAR(30);

COMMENT ON COLUMN fdc_application_t.status IS '申请状态（草稿/已提交）';

UPDATE fdc_application_t
SET status = application_status
WHERE status IS NULL
  AND application_status IS NOT NULL;
