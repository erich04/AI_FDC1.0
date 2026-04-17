CREATE TABLE IF NOT EXISTS fdc_company_info_t (
    company_id BIGSERIAL PRIMARY KEY,
    company_code VARCHAR(64) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    region VARCHAR(128),
    representative_office VARCHAR(128),
    country VARCHAR(128),
    description VARCHAR(500),
    tags VARCHAR(1000),
    enabled_flag CHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_company_info_t_code
    ON fdc_company_info_t (company_code)
    WHERE delete_flag = 'N';

CREATE TABLE IF NOT EXISTS fdc_company_tag_t (
    tag_id BIGSERIAL PRIMARY KEY,
    tag_value VARCHAR(128) NOT NULL,
    enabled_flag CHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_company_tag_t_value
    ON fdc_company_tag_t (tag_value)
    WHERE delete_flag = 'N';

CREATE TABLE IF NOT EXISTS fdc_business_module_t (
    business_module_id BIGSERIAL PRIMARY KEY,
    module_code VARCHAR(64) NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    parent_code VARCHAR(64),
    level_num INTEGER NOT NULL DEFAULT 1,
    ancestor_path VARCHAR(500),
    enabled_flag CHAR(1) NOT NULL DEFAULT 'Y',
    security_level VARCHAR(32) NOT NULL DEFAULT '公开',
    integration_type VARCHAR(32) NOT NULL DEFAULT '不集成',
    description VARCHAR(500),
    remark VARCHAR(500),
    sort_order INTEGER NOT NULL DEFAULT 1,
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_business_module_t_code
    ON fdc_business_module_t (module_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_fdc_business_module_t_parent_code
    ON fdc_business_module_t (parent_code);

CREATE TABLE IF NOT EXISTS fdc_business_module_ext_field_t (
    field_id BIGSERIAL PRIMARY KEY,
    field_code VARCHAR(64) NOT NULL,
    module_code VARCHAR(64) NOT NULL,
    field_scope VARCHAR(32) NOT NULL,
    application_functions VARCHAR(128) NOT NULL,
    ext_attribute VARCHAR(32) NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    english_field_name VARCHAR(255),
    data_type VARCHAR(32) NOT NULL,
    query_flag CHAR(1) NOT NULL DEFAULT 'N',
    required_flag CHAR(1) NOT NULL DEFAULT 'N',
    enabled_flag CHAR(1) NOT NULL DEFAULT 'Y',
    sort_order INTEGER NOT NULL DEFAULT 1,
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_business_module_ext_field_t_code
    ON fdc_business_module_ext_field_t (field_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_fdc_business_module_ext_field_t_module_code
    ON fdc_business_module_ext_field_t (module_code);
