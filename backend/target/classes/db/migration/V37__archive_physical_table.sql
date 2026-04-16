CREATE SEQUENCE IF NOT EXISTS fdc_archive_physical_t_arch_physical_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS fdc_archive_physical_t (
    arch_physical_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_archive_physical_t_arch_physical_id_seq'),
    doc_id BIGINT NOT NULL,
    arch_physical_no VARCHAR(60),
    volume_id BIGINT,
    vol_seq_no VARCHAR(60),
    bound_time TIMESTAMP,
    bound_by BIGINT,
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
    CONSTRAINT ck_fdc_archive_physical_t_enable_flag CHECK (enable_flag IN ('Y', 'N')),
    CONSTRAINT ck_fdc_archive_physical_t_delete_flag CHECK (delete_flag IN ('Y', 'N'))
);

COMMENT ON TABLE fdc_archive_physical_t IS '档案物理信息表';
COMMENT ON COLUMN fdc_archive_physical_t.arch_physical_id IS '档案物理ID';
COMMENT ON COLUMN fdc_archive_physical_t.doc_id IS '文档ID';
COMMENT ON COLUMN fdc_archive_physical_t.arch_physical_no IS '实物档案号';
COMMENT ON COLUMN fdc_archive_physical_t.volume_id IS '册ID';
COMMENT ON COLUMN fdc_archive_physical_t.vol_seq_no IS '档案在册内的顺序号';
COMMENT ON COLUMN fdc_archive_physical_t.bound_time IS '档案成册时间';
COMMENT ON COLUMN fdc_archive_physical_t.bound_by IS '档案成册人（逻辑外键到 tpl_user_t.user_id）';
COMMENT ON COLUMN fdc_archive_physical_t.inbound_time IS '档案入库时间';
COMMENT ON COLUMN fdc_archive_physical_t.inbound_by IS '档案入库人（逻辑外键到 tpl_user_t.user_id）';

CREATE INDEX IF NOT EXISTS idx_fdc_archive_physical_tn1
    ON fdc_archive_physical_t (tenantid, doc_id);

CREATE INDEX IF NOT EXISTS idx_fdc_archive_physical_tn2
    ON fdc_archive_physical_t (tenantid, volume_id);

CREATE INDEX IF NOT EXISTS idx_fdc_archive_physical_tn3
    ON fdc_archive_physical_t (tenantid, arch_physical_no);
