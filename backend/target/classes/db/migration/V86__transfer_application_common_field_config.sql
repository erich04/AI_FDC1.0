CREATE TABLE IF NOT EXISTS fdc_transfer_apply_field_cfg_t (
    config_id BIGSERIAL PRIMARY KEY,
    document_type_code VARCHAR(60) NOT NULL,
    field_code VARCHAR(60) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    visible_flag CHAR(1) NOT NULL DEFAULT 'Y',
    sort_order INT4 NOT NULL DEFAULT 1,
    enable_flag CHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_description VARCHAR(500),
    last_update_trace_id VARCHAR(100),
    tenantid BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT uk_fdc_transfer_apply_field_cfg_t UNIQUE (tenantid, document_type_code, field_code),
    CONSTRAINT ck_fdc_transfer_apply_field_cfg_t_visible CHECK (visible_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_transfer_apply_field_cfg_t_enable CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_transfer_apply_field_cfg_t_delete CHECK (delete_flag IN ('Y', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_transfer_apply_field_cfg_tn1
    ON fdc_transfer_apply_field_cfg_t (tenantid, document_type_code, delete_flag, enable_flag);

INSERT INTO fdc_transfer_apply_field_cfg_t (
    document_type_code,
    field_code,
    field_name,
    visible_flag,
    sort_order,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    tenantid
)
SELECT
    bm.module_code AS document_type_code,
    f.field_code,
    f.field_name,
    'Y' AS visible_flag,
    f.sort_order,
    'Y' AS enable_flag,
    'N' AS delete_flag,
    1 AS created_by,
    CURRENT_TIMESTAMP AS creation_date,
    1 AS last_updated_by,
    CURRENT_TIMESTAMP AS last_update_date,
    1 AS tenantid
FROM fdc_business_module_t bm
CROSS JOIN (
    VALUES
        ('companyProjectCode', '公司', 10),
        ('docBusiNo', '文档业务编码', 20),
        ('docName', '文档名称', 30),
        ('busiModuleCode', '业务模块', 40),
        ('archPlaceAlpha2Code', '归档地', 50),
        ('documentOrganizationCode', '文档组织', 60),
        ('startArchPeriod', '开始档期', 70),
        ('endArchPeriod', '结束档期', 80),
        ('docGenerationDate', '文档生成日期', 90),
        ('carrierType', '载体类型', 100),
        ('archCopies', '份数', 110),
        ('remark', '备注', 120),
        ('description', '描述', 130)
) AS f(field_code, field_name, sort_order)
WHERE bm.level_num = 1
  AND bm.delete_flag = 'N'
  AND bm.enabled_flag = 'Y'
ON CONFLICT (tenantid, document_type_code, field_code) DO NOTHING;
