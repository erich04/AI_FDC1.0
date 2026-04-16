-- 补充“非会计文档类型”的演示归档流向规则（幂等）
-- 依赖：V53 已准备 CP-DEMO-001、DO-DEMO-SH、DO-DEMO-BJ

-- 1) 资金文档：付款申请与指令 + 上海
INSERT INTO fdc_archive_rule_t (
    company_project_code, document_type_code, custom_rule, archive_destination,
    custom_rule_normalized, archive_destination_normalized,
    document_organization_code, retention_period_years, security_level_code,
    external_display_flag, enable_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
)
SELECT
    'CP-DEMO-001', 'FIN_FUND', 'FIN_FUND_PAYMENT_PAY', 'SHANGHAI',
    'FIN_FUND_PAYMENT_PAY', 'SHANGHAI',
    'DO-DEMO-SH', 7, 'INTERNAL',
    'N', 'Y', 'N',
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
      from fdc_archive_rule_t r
     where r.company_project_code = 'CP-DEMO-001'
       and r.document_type_code = 'FIN_FUND'
       and r.custom_rule_normalized = 'FIN_FUND_PAYMENT_PAY'
       and r.archive_destination_normalized = 'SHANGHAI'
       and r.delete_flag = 'N'
);

-- 2) 资金文档兜底：不限模块/归档地
INSERT INTO fdc_archive_rule_t (
    company_project_code, document_type_code, custom_rule, archive_destination,
    custom_rule_normalized, archive_destination_normalized,
    document_organization_code, retention_period_years, security_level_code,
    external_display_flag, enable_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
)
SELECT
    'CP-DEMO-001', 'FIN_FUND', NULL, NULL,
    '', '',
    'DO-DEMO-SH', 7, 'PUBLIC',
    'N', 'Y', 'N',
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
      from fdc_archive_rule_t r
     where r.company_project_code = 'CP-DEMO-001'
       and r.document_type_code = 'FIN_FUND'
       and r.custom_rule_normalized = ''
       and r.archive_destination_normalized = ''
       and r.delete_flag = 'N'
);

-- 3) 财经其他文档：内部审计 + 北京
INSERT INTO fdc_archive_rule_t (
    company_project_code, document_type_code, custom_rule, archive_destination,
    custom_rule_normalized, archive_destination_normalized,
    document_organization_code, retention_period_years, security_level_code,
    external_display_flag, enable_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
)
SELECT
    'CP-DEMO-001', 'FIN_OTHER', 'FIN_OTHER_AUDIT_INTERNAL', 'BEIJING',
    'FIN_OTHER_AUDIT_INTERNAL', 'BEIJING',
    'DO-DEMO-BJ', 10, 'INTERNAL',
    'N', 'Y', 'N',
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
      from fdc_archive_rule_t r
     where r.company_project_code = 'CP-DEMO-001'
       and r.document_type_code = 'FIN_OTHER'
       and r.custom_rule_normalized = 'FIN_OTHER_AUDIT_INTERNAL'
       and r.archive_destination_normalized = 'BEIJING'
       and r.delete_flag = 'N'
);

-- 4) 财经其他文档兜底：不限模块/归档地
INSERT INTO fdc_archive_rule_t (
    company_project_code, document_type_code, custom_rule, archive_destination,
    custom_rule_normalized, archive_destination_normalized,
    document_organization_code, retention_period_years, security_level_code,
    external_display_flag, enable_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
)
SELECT
    'CP-DEMO-001', 'FIN_OTHER', NULL, NULL,
    '', '',
    'DO-DEMO-BJ', 10, 'PUBLIC',
    'N', 'Y', 'N',
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
      from fdc_archive_rule_t r
     where r.company_project_code = 'CP-DEMO-001'
       and r.document_type_code = 'FIN_OTHER'
       and r.custom_rule_normalized = ''
       and r.archive_destination_normalized = ''
       and r.delete_flag = 'N'
);

-- 5) 非财经文档：合同文本 + 北京
INSERT INTO fdc_archive_rule_t (
    company_project_code, document_type_code, custom_rule, archive_destination,
    custom_rule_normalized, archive_destination_normalized,
    document_organization_code, retention_period_years, security_level_code,
    external_display_flag, enable_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
)
SELECT
    'CP-DEMO-001', 'NON_FIN', 'NON_FIN_LEGAL_CONTRACT', 'BEIJING',
    'NON_FIN_LEGAL_CONTRACT', 'BEIJING',
    'DO-DEMO-BJ', 15, 'SECRET',
    'N', 'Y', 'N',
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
      from fdc_archive_rule_t r
     where r.company_project_code = 'CP-DEMO-001'
       and r.document_type_code = 'NON_FIN'
       and r.custom_rule_normalized = 'NON_FIN_LEGAL_CONTRACT'
       and r.archive_destination_normalized = 'BEIJING'
       and r.delete_flag = 'N'
);

-- 6) 非财经文档：项目报告与纪要 + 上海
INSERT INTO fdc_archive_rule_t (
    company_project_code, document_type_code, custom_rule, archive_destination,
    custom_rule_normalized, archive_destination_normalized,
    document_organization_code, retention_period_years, security_level_code,
    external_display_flag, enable_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
)
SELECT
    'CP-DEMO-001', 'NON_FIN', 'NON_FIN_PROJECT_REPORT', 'SHANGHAI',
    'NON_FIN_PROJECT_REPORT', 'SHANGHAI',
    'DO-DEMO-SH', 5, 'INTERNAL',
    'N', 'Y', 'N',
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
      from fdc_archive_rule_t r
     where r.company_project_code = 'CP-DEMO-001'
       and r.document_type_code = 'NON_FIN'
       and r.custom_rule_normalized = 'NON_FIN_PROJECT_REPORT'
       and r.archive_destination_normalized = 'SHANGHAI'
       and r.delete_flag = 'N'
);

-- 7) 非财经文档兜底：不限模块/归档地
INSERT INTO fdc_archive_rule_t (
    company_project_code, document_type_code, custom_rule, archive_destination,
    custom_rule_normalized, archive_destination_normalized,
    document_organization_code, retention_period_years, security_level_code,
    external_display_flag, enable_flag, delete_flag,
    created_by, creation_date, last_updated_by, last_update_date
)
SELECT
    'CP-DEMO-001', 'NON_FIN', NULL, NULL,
    '', '',
    'DO-DEMO-SH', 5, 'PUBLIC',
    'N', 'Y', 'N',
    1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
      from fdc_archive_rule_t r
     where r.company_project_code = 'CP-DEMO-001'
       and r.document_type_code = 'NON_FIN'
       and r.custom_rule_normalized = ''
       and r.archive_destination_normalized = ''
       and r.delete_flag = 'N'
);
