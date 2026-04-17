DO $$
BEGIN
    IF to_regclass('public.fdc_archive_rule_t') IS NULL THEN
        RETURN;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'company_project_code'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'company_code'
    ) THEN
        EXECUTE 'ALTER TABLE fdc_archive_rule_t RENAME COLUMN company_project_code TO company_code';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'document_type_code'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'module_code'
    ) THEN
        EXECUTE 'ALTER TABLE fdc_archive_rule_t RENAME COLUMN document_type_code TO module_code';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'custom_rule'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'cust_mapping_code'
    ) THEN
        EXECUTE 'ALTER TABLE fdc_archive_rule_t RENAME COLUMN custom_rule TO cust_mapping_code';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'archive_destination'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'arch_place_alpha2_code'
    ) THEN
        EXECUTE 'ALTER TABLE fdc_archive_rule_t RENAME COLUMN archive_destination TO arch_place_alpha2_code';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'retention_period_years'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'retention_term'
    ) THEN
        EXECUTE 'ALTER TABLE fdc_archive_rule_t RENAME COLUMN retention_period_years TO retention_term';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'external_display_flag'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'fdc_archive_rule_t' AND column_name = 'visible_flag'
    ) THEN
        EXECUTE 'ALTER TABLE fdc_archive_rule_t RENAME COLUMN external_display_flag TO visible_flag';
    END IF;
END $$;

ALTER TABLE fdc_archive_rule_t
    ADD COLUMN IF NOT EXISTS default_flag VARCHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE fdc_archive_rule_t
    DROP COLUMN IF EXISTS security_level_code,
    DROP COLUMN IF EXISTS custom_rule_normalized,
    DROP COLUMN IF EXISTS archive_destination_normalized;

ALTER TABLE fdc_archive_rule_t
    DROP CONSTRAINT IF EXISTS uk_md_archive_flow_rule_business;

DROP INDEX IF EXISTS idx_md_archive_flow_rule_type;
DROP INDEX IF EXISTS idx_md_archive_flow_rule_security;
DROP INDEX IF EXISTS idx_fdc_archive_rule_tn_type;
DROP INDEX IF EXISTS idx_fdc_archive_rule_tn_security;

CREATE INDEX IF NOT EXISTS idx_fdc_archive_rule_tn_type
    ON fdc_archive_rule_t (module_code);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_archive_rule_business
    ON fdc_archive_rule_t (
        company_code,
        module_code,
        COALESCE(NULLIF(BTRIM(cust_mapping_code), ''), ''),
        COALESCE(NULLIF(BTRIM(arch_place_alpha2_code), ''), '')
    )
    WHERE delete_flag = 'N';
