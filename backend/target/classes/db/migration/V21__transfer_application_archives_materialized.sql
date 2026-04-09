ALTER TABLE fdc_application_t ADD COLUMN IF NOT EXISTS archives_materialized CHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE fdc_application_t DROP CONSTRAINT IF EXISTS ck_fdc_application_t_archives_materialized;

ALTER TABLE fdc_application_t
    ADD CONSTRAINT ck_fdc_application_t_archives_materialized CHECK (archives_materialized IN ('Y', 'N'));

COMMENT ON COLUMN fdc_application_t.archives_materialized IS '移交审批通过后是否已将明细写入档案库（fdc_arch_t）';
