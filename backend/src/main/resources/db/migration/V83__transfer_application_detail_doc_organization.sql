-- 移交申请行：文档组织（与归档规则匹配结果一致，可编辑保存）
ALTER TABLE fdc_application_detail_t ADD COLUMN IF NOT EXISTS doc_organization_code VARCHAR(64);

COMMENT ON COLUMN fdc_application_detail_t.doc_organization_code IS '文档组织编码（归档规则匹配或手工维护）';
