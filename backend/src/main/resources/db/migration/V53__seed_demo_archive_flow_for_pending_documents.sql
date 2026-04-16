-- 演示数据：子公司、文档组织、归档流向规则，便于「应归档」创建页通过 resolveDefaults 匹配文档组织/密级等。
-- 公司编码 CP-DEMO-001；文档类型 FIN_ACC；业务模块示例 FIN_ACC_VCH_AP；归档地 SHANGHAI / BEIJING（与 fdc_document_organization_city_t 种子一致）。

-- 1) 演示子公司
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
      AND c.delete_flag = 'N'
);

-- 2) 演示文档组织（归档流向中的 document_organization_code 须指向有效组织）
INSERT INTO fdc_document_organization_t (
    document_organization_code,
    document_organization_name,
    description,
    country_code,
    city_code,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date
)
SELECT
    'DO-DEMO-SH',
    '演示文档组织-上海',
    '演示：上海归档中心',
    'CN',
    'SHANGHAI',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_document_organization_t o
    WHERE o.document_organization_code = 'DO-DEMO-SH'
      AND o.delete_flag = 'N'
);

INSERT INTO fdc_document_organization_t (
    document_organization_code,
    document_organization_name,
    description,
    country_code,
    city_code,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date
)
SELECT
    'DO-DEMO-BJ',
    '演示文档组织-北京',
    '演示：北京归档中心',
    'CN',
    'BEIJING',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_document_organization_t o
    WHERE o.document_organization_code = 'DO-DEMO-BJ'
      AND o.delete_flag = 'N'
);

-- 3) 归档流向规则（须与 uk_md_archive_flow_rule_business 一致：维护 normalized 列）
-- 3a) 精确：会计文档 + 三级模块「应付凭证」+ 归档地上海 -> 上海组织
INSERT INTO fdc_archive_rule_t (
    company_project_code,
    document_type_code,
    custom_rule,
    archive_destination,
    custom_rule_normalized,
    archive_destination_normalized,
    document_organization_code,
    retention_period_years,
    security_level_code,
    external_display_flag,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date
)
SELECT
    'CP-DEMO-001',
    'FIN_ACC',
    'FIN_ACC_VCH_AP',
    'SHANGHAI',
    'FIN_ACC_VCH_AP',
    'SHANGHAI',
    'DO-DEMO-SH',
    10,
    'INTERNAL',
    'N',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_archive_rule_t r
    WHERE r.company_project_code = 'CP-DEMO-001'
      AND r.document_type_code = 'FIN_ACC'
      AND r.custom_rule_normalized = 'FIN_ACC_VCH_AP'
      AND r.archive_destination_normalized = 'SHANGHAI'
      AND r.delete_flag = 'N'
);

-- 3b) 精确：同模块 + 北京 -> 北京组织
INSERT INTO fdc_archive_rule_t (
    company_project_code,
    document_type_code,
    custom_rule,
    archive_destination,
    custom_rule_normalized,
    archive_destination_normalized,
    document_organization_code,
    retention_period_years,
    security_level_code,
    external_display_flag,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date
)
SELECT
    'CP-DEMO-001',
    'FIN_ACC',
    'FIN_ACC_VCH_AP',
    'BEIJING',
    'FIN_ACC_VCH_AP',
    'BEIJING',
    'DO-DEMO-BJ',
    10,
    'INTERNAL',
    'N',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_archive_rule_t r
    WHERE r.company_project_code = 'CP-DEMO-001'
      AND r.document_type_code = 'FIN_ACC'
      AND r.custom_rule_normalized = 'FIN_ACC_VCH_AP'
      AND r.archive_destination_normalized = 'BEIJING'
      AND r.delete_flag = 'N'
);

-- 3c) 兜底：同公司 + 会计文档，不限制业务模块与归档地（normalized 均为空串）
INSERT INTO fdc_archive_rule_t (
    company_project_code,
    document_type_code,
    custom_rule,
    archive_destination,
    custom_rule_normalized,
    archive_destination_normalized,
    document_organization_code,
    retention_period_years,
    security_level_code,
    external_display_flag,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date
)
SELECT
    'CP-DEMO-001',
    'FIN_ACC',
    NULL,
    NULL,
    '',
    '',
    'DO-DEMO-SH',
    10,
    'PUBLIC',
    'N',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_archive_rule_t r
    WHERE r.company_project_code = 'CP-DEMO-001'
      AND r.document_type_code = 'FIN_ACC'
      AND r.custom_rule_normalized = ''
      AND r.archive_destination_normalized = ''
      AND r.delete_flag = 'N'
);

-- 3d) 税务文档示例（任选 FIN_TAX 下三级模块如 FIN_TAX_FILING_VAT + 深圳）
INSERT INTO fdc_archive_rule_t (
    company_project_code,
    document_type_code,
    custom_rule,
    archive_destination,
    custom_rule_normalized,
    archive_destination_normalized,
    document_organization_code,
    retention_period_years,
    security_level_code,
    external_display_flag,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date
)
SELECT
    'CP-DEMO-001',
    'FIN_TAX',
    'FIN_TAX_FILING_VAT',
    'SHENZHEN',
    'FIN_TAX_FILING_VAT',
    'SHENZHEN',
    'DO-DEMO-SH',
    7,
    'SECRET',
    'N',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_archive_rule_t r
    WHERE r.company_project_code = 'CP-DEMO-001'
      AND r.document_type_code = 'FIN_TAX'
      AND r.custom_rule_normalized = 'FIN_TAX_FILING_VAT'
      AND r.archive_destination_normalized = 'SHENZHEN'
      AND r.delete_flag = 'N'
);
