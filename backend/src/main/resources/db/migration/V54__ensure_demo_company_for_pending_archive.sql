-- 确保演示子公司 CP-DEMO-001 在「应归档/档案创建」公司下拉中可见。
-- listEnabledCompanyProjects 仅查询 enable_flag='Y' 且 delete_flag='N'。
-- 仅当库中尚无任何该编码行时才 INSERT；已有行（含软删）由 UPDATE 统一启用。

INSERT INTO fdc_company_project_t (
    company_project_code,
    company_project_name,
    country_code,
    management_area,
    company_tag,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date
)
SELECT
    'CP-DEMO-001',
    '演示子公司（中国）',
    'CN',
    '华东',
    '演示',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_company_project_t c
    WHERE c.company_project_code = 'CP-DEMO-001'
);

-- 已存在但未启用、或曾被软删的记录：统一改为启用并在库中可见
UPDATE fdc_company_project_t
SET company_project_name = '演示子公司（中国）',
    country_code = 'CN',
    management_area = '华东',
    company_tag = COALESCE(NULLIF(BTRIM(company_tag), ''), '演示'),
    enable_flag = 'Y',
    delete_flag = 'N',
    last_updated_by = 1,
    last_update_date = CURRENT_TIMESTAMP
WHERE company_project_code = 'CP-DEMO-001';
