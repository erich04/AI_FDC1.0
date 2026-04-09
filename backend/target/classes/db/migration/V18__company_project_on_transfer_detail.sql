-- 将公司/项目编码从申请头迁至申请明细
ALTER TABLE fdc_application_detail_t ADD COLUMN IF NOT EXISTS company_project_code VARCHAR(30);

COMMENT ON COLUMN fdc_application_detail_t.company_project_code IS '公司/项目编码';

UPDATE fdc_application_detail_t d
SET company_project_code = a.company_project_code
FROM fdc_application_t a
WHERE d.application_id = a.application_id
  AND d.delete_flag = 'N'
  AND a.company_project_code IS NOT NULL
  AND btrim(a.company_project_code) <> '';

ALTER TABLE fdc_application_t DROP COLUMN IF EXISTS company_project_code;
