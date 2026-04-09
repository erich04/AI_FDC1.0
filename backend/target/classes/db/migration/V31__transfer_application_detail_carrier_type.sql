ALTER TABLE fdc_application_detail_t
    ADD COLUMN IF NOT EXISTS carrier_type VARCHAR(30);

CREATE INDEX IF NOT EXISTS idx_fdc_application_detail_tn3
    ON fdc_application_detail_t (tenantid, carrier_type);
