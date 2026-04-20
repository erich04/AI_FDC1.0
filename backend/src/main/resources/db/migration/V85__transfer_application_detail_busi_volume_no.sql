ALTER TABLE fdc_application_detail_t ADD COLUMN IF NOT EXISTS busi_volume_no VARCHAR(120);

COMMENT ON COLUMN fdc_application_detail_t.busi_volume_no IS '业务册号（按册移交时填写）';
