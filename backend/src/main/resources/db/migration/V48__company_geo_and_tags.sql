ALTER TABLE fdc_company_project_t
    ADD COLUMN IF NOT EXISTS company_tag VARCHAR(128);

ALTER TABLE fdc_company_project_t
    ADD COLUMN IF NOT EXISTS rep_office_code VARCHAR(32);

ALTER TABLE fdc_company_project_t
    ADD COLUMN IF NOT EXISTS region_code VARCHAR(32);

CREATE TABLE IF NOT EXISTS fdc_geo_region_t (
    geo_region_id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(32) NOT NULL,
    rep_office_code VARCHAR(32) NOT NULL,
    rep_office_name VARCHAR(128) NOT NULL,
    region_code VARCHAR(32) NOT NULL,
    region_name VARCHAR(128) NOT NULL,
    enable_flag CHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_version INT4 NOT NULL DEFAULT 0,
    sys_description VARCHAR(500),
    last_update_trace_id VARCHAR(100),
    tenantid BIGINT NOT NULL DEFAULT 1
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_geo_region_t_active
    ON fdc_geo_region_t (country_code, rep_office_code, region_code)
    WHERE delete_flag = 'N';

CREATE INDEX IF NOT EXISTS idx_fdc_geo_region_tn1
    ON fdc_geo_region_t (country_code, rep_office_code, enable_flag, delete_flag);
