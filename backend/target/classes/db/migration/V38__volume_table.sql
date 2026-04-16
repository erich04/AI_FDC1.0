CREATE SEQUENCE IF NOT EXISTS fdc_volume_t_volume_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS fdc_volume_t (
    volume_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_volume_t_volume_id_seq'),
    location_id BIGINT,
    volume_barcode VARCHAR(60),
    volume_code VARCHAR(60) NOT NULL,
    volume_status VARCHAR(30),
    inbound_time TIMESTAMP,
    inbound_by BIGINT,
    enable_flag CHAR(1) NOT NULL DEFAULT 'Y',
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP,
    sys_description VARCHAR(500),
    last_update_trace_id VARCHAR(100),
    last_update_version INT4 NOT NULL DEFAULT 0,
    tenantid BIGINT NOT NULL,
    CONSTRAINT uk_fdc_volume_t UNIQUE (tenantid, volume_code),
    CONSTRAINT ck_fdc_volume_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_volume_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

COMMENT ON TABLE fdc_volume_t IS '册信息表';
COMMENT ON COLUMN fdc_volume_t.volume_id IS '册ID';
COMMENT ON COLUMN fdc_volume_t.location_id IS '册所在库位ID';
COMMENT ON COLUMN fdc_volume_t.volume_barcode IS '册条码';
COMMENT ON COLUMN fdc_volume_t.volume_code IS '册号';
COMMENT ON COLUMN fdc_volume_t.volume_status IS '册状态';
COMMENT ON COLUMN fdc_volume_t.inbound_time IS '册入库时间';
COMMENT ON COLUMN fdc_volume_t.inbound_by IS '册入库人（逻辑外键到 tpl_user_t.user_id）';

CREATE INDEX IF NOT EXISTS idx_fdc_volume_tn1
    ON fdc_volume_t (tenantid, location_id);

CREATE INDEX IF NOT EXISTS idx_fdc_volume_tn2
    ON fdc_volume_t (tenantid, volume_status);

CREATE INDEX IF NOT EXISTS idx_fdc_volume_tn3
    ON fdc_volume_t (tenantid, inbound_time);
