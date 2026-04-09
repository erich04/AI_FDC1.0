ALTER TABLE fdc_doc_field_config_t
    ADD COLUMN usage_module VARCHAR(64),
    ADD COLUMN related_module_code VARCHAR(64),
    ADD COLUMN related_module VARCHAR(128);

UPDATE fdc_doc_field_config_t
SET usage_module = COALESCE(NULLIF(TRIM(usage_module), ''), 'UNSPECIFIED'),
    related_module_code = COALESCE(NULLIF(TRIM(related_module_code), ''), 'UNSPECIFIED'),
    related_module = COALESCE(NULLIF(TRIM(related_module), ''), 'UNSPECIFIED')
WHERE delete_flag = 'N';

ALTER TABLE fdc_doc_field_config_t
    ALTER COLUMN usage_module SET NOT NULL,
    ALTER COLUMN related_module_code SET NOT NULL,
    ALTER COLUMN related_module SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_doc_field_cfg_usage_module
    ON fdc_doc_field_config_t (usage_module, related_module_code, delete_flag);
