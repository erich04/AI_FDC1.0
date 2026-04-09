-- 移交记录查询：申请头扩展字段、明细册号
ALTER TABLE fdc_application_t ADD COLUMN IF NOT EXISTS application_status VARCHAR(30);
ALTER TABLE fdc_application_t ADD COLUMN IF NOT EXISTS diff_reason_code VARCHAR(30);
ALTER TABLE fdc_application_t ADD COLUMN IF NOT EXISTS company_project_code VARCHAR(30);

ALTER TABLE fdc_application_detail_t ADD COLUMN IF NOT EXISTS catalog_volume_no VARCHAR(60);

COMMENT ON COLUMN fdc_application_t.application_status IS '申请状态（快码）';
COMMENT ON COLUMN fdc_application_t.diff_reason_code IS '差异原因（快码）';
COMMENT ON COLUMN fdc_application_t.company_project_code IS '公司/项目编码';
COMMENT ON COLUMN fdc_application_detail_t.catalog_volume_no IS '册号';
