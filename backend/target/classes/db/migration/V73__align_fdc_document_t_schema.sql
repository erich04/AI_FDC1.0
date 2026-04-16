-- Align fdc_document_t with latest data model definition.
-- Keep historical migration V33 immutable; apply incremental change here.

ALTER TABLE IF EXISTS fdc_document_attach_t DROP CONSTRAINT IF EXISTS fk_doc_att_document;
ALTER TABLE IF EXISTS fdc_arch_storage_t DROP CONSTRAINT IF EXISTS fk_arch_storage_document;

ALTER TABLE fdc_document_t RENAME TO fdc_document_t_legacy_v33;

CREATE TABLE fdc_document_t (
    doc_id BIGINT PRIMARY KEY,
    company_code VARCHAR(60) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    start_period DATE NOT NULL,
    end_period DATE,
    biz_module_code VARCHAR(30) NOT NULL,
    doc_biz_no VARCHAR(100) NOT NULL,
    doc_gen_date TIMESTAMP NOT NULL,
    arch_place_alpha2_code VARCHAR(60) NOT NULL,
    origin_place_alpha2_code VARCHAR(60) NOT NULL,
    carrier_type VARCHAR(30) NOT NULL,
    doc_name VARCHAR(100) NOT NULL,
    doc_organization_code VARCHAR(60) NOT NULL,
    doc_resp_dept_id BIGINT NOT NULL,
    doc_resp_person_id BIGINT NOT NULL,
    rentention_term INT4 NOT NULL,
    security_level VARCHAR(30) NOT NULL,
    doc_version VARCHAR(100) NOT NULL,
    source_id VARCHAR(500),
    source_system VARCHAR(30) NOT NULL,
    lifecycle_status VARCHAR(30) NOT NULL,
    custody_status VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    copies_qty INT4,
    remaining_copies_qty INT4,
    integretion_time TIMESTAMP,
    received_by BIGINT,
    received_time TIMESTAMP,
    verified_by BIGINT,
    verification_time TIMESTAMP,
    arch_barcode VARCHAR(100),
    arch_description VARCHAR(500),
    arch_type_code VARCHAR(60),
    attachment_qty INT4,
    attr1 VARCHAR(100), attr2 VARCHAR(100), attr3 VARCHAR(100), attr4 VARCHAR(100), attr5 VARCHAR(100),
    attr6 VARCHAR(100), attr7 VARCHAR(100), attr8 VARCHAR(100), attr9 VARCHAR(100), attr10 VARCHAR(100),
    attr11 VARCHAR(100), attr12 VARCHAR(100), attr13 VARCHAR(100), attr14 VARCHAR(100), attr15 VARCHAR(100),
    attr16 VARCHAR(100), attr17 VARCHAR(100), attr18 VARCHAR(100), attr19 VARCHAR(100), attr20 VARCHAR(100),
    attr21 VARCHAR(100), attr22 VARCHAR(100), attr23 VARCHAR(100), attr24 VARCHAR(100), attr25 VARCHAR(100),
    attr26 VARCHAR(100), attr27 VARCHAR(100), attr28 VARCHAR(100), attr29 VARCHAR(100), attr30 VARCHAR(100),
    attr31 VARCHAR(100), attr32 VARCHAR(100), attr33 VARCHAR(100), attr34 VARCHAR(100),
    attr35 NUMERIC(38,10), attr36 NUMERIC(38,10), attr37 NUMERIC(38,10), attr38 NUMERIC(38,10), attr39 NUMERIC(38,10),
    attr40 NUMERIC(38,10), attr41 NUMERIC(38,10), attr42 NUMERIC(38,10), attr43 NUMERIC(38,10), attr44 NUMERIC(38,10),
    attr45 NUMERIC(38,10), attr46 NUMERIC(38,10), attr47 NUMERIC(38,10), attr48 NUMERIC(38,10), attr49 NUMERIC(38,10),
    attr50 NUMERIC(38,10), attr51 NUMERIC(38,10), attr52 NUMERIC(38,10), attr53 NUMERIC(38,10), attr54 NUMERIC(38,10),
    attr55 NUMERIC(38,10), attr56 NUMERIC(38,10), attr57 NUMERIC(38,10), attr58 NUMERIC(38,10), attr59 NUMERIC(38,10),
    attr60 NUMERIC(38,10), attr61 NUMERIC(38,10), attr62 NUMERIC(38,10), attr63 NUMERIC(38,10), attr64 NUMERIC(38,10),
    attr65 NUMERIC(38,10), attr66 NUMERIC(38,10), attr67 NUMERIC(38,10),
    attr68 TIMESTAMP, attr69 TIMESTAMP, attr70 TIMESTAMP, attr71 TIMESTAMP, attr72 TIMESTAMP,
    attr73 TIMESTAMP, attr74 TIMESTAMP, attr75 TIMESTAMP, attr76 TIMESTAMP, attr77 TIMESTAMP,
    attr78 TIMESTAMP, attr79 TIMESTAMP, attr80 TIMESTAMP, attr81 TIMESTAMP, attr82 TIMESTAMP,
    attr83 TIMESTAMP, attr84 TIMESTAMP, attr85 TIMESTAMP, attr86 TIMESTAMP, attr87 TIMESTAMP,
    attr88 TIMESTAMP, attr89 TIMESTAMP, attr90 TIMESTAMP, attr91 TIMESTAMP, attr92 TIMESTAMP,
    attr93 TIMESTAMP, attr94 TIMESTAMP, attr95 TIMESTAMP, attr96 TIMESTAMP, attr97 TIMESTAMP,
    attr98 TIMESTAMP, attr99 TIMESTAMP, attr100 TIMESTAMP,
    delete_flag SMALLINT NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    last_updated_by BIGINT NOT NULL,
    last_update_date TIMESTAMP NOT NULL,
    sys_description VARCHAR(500),
    last_update_trace_id VARCHAR(100),
    tenantid BIGINT
);

INSERT INTO fdc_document_t (
    doc_id, company_code, company_name, start_period, end_period, biz_module_code, doc_biz_no, doc_gen_date,
    arch_place_alpha2_code, origin_place_alpha2_code, carrier_type, doc_name, doc_organization_code,
    doc_resp_dept_id, doc_resp_person_id, rentention_term, security_level, doc_version, source_id, source_system,
    lifecycle_status, custody_status, description, copies_qty, remaining_copies_qty, integretion_time,
    received_by, received_time, verified_by, verification_time, arch_barcode, arch_description, arch_type_code,
    attachment_qty, attr1, attr2, attr3, attr4, attr5, attr6, attr7, attr8, attr9, attr10, attr11, attr12, attr13,
    attr14, attr15, attr16, attr17, attr18, attr19, attr20, attr21, attr22, attr23, attr24, attr25, attr26,
    attr27, attr28, attr29, attr30, attr31, attr32, attr33, attr34, delete_flag, created_by, creation_date,
    last_updated_by, last_update_date, tenantid
)
SELECT
    document_id,
    COALESCE(NULLIF(attribute1, ''), 'UNKNOWN'),
    COALESCE(NULLIF(attribute2, ''), 'UNKNOWN'),
    COALESCE(sign_time::DATE, creation_date::DATE),
    write_off_time::DATE,
    COALESCE(business_module_id::VARCHAR, '0'),
    doc_busi_no,
    COALESCE(sign_time, creation_date),
    COALESCE(NULLIF(attribute3, ''), 'CN'),
    COALESCE(NULLIF(attribute4, ''), 'CN'),
    COALESCE(NULLIF(attribute5, ''), 'UNKNOWN'),
    LEFT(doc_name, 100),
    COALESCE(NULLIF(attribute6, ''), 'UNKNOWN'),
    COALESCE(owner_id, 0),
    COALESCE(owner_id, 0),
    0,
    COALESCE(NULLIF(attribute8, ''), 'INTERNAL'),
    '1.0',
    NULL,
    'LEGACY',
    COALESCE(doc_status, 'INIT'),
    COALESCE(doc_status, 'INIT'),
    NULL,
    NULL,
    NULL,
    creation_date,
    signer,
    sign_time,
    write_off_by,
    write_off_time,
    archive_barcode,
    NULL,
    document_type_id::VARCHAR,
    NULL,
    LEFT(attribute1, 100), LEFT(attribute2, 100), LEFT(attribute3, 100), LEFT(attribute4, 100), LEFT(attribute5, 100),
    LEFT(attribute6, 100), LEFT(attribute7, 100), LEFT(attribute8, 100), LEFT(attribute9, 100), LEFT(attribute10, 100),
    LEFT(attribute11, 100), LEFT(attribute12, 100), LEFT(attribute13, 100), LEFT(attribute14, 100), LEFT(attribute15, 100),
    LEFT(attribute16, 100), LEFT(attribute17, 100), LEFT(attribute18, 100), LEFT(attribute19, 100), LEFT(attribute20, 100),
    LEFT(attribute21, 100), LEFT(attribute22, 100), LEFT(attribute23, 100), LEFT(attribute24, 100), LEFT(attribute25, 100),
    LEFT(attribute26, 100), LEFT(attribute27, 100), LEFT(attribute28, 100), LEFT(attribute29, 100), LEFT(attribute30, 100),
    LEFT(attribute31, 100), LEFT(attribute32, 100), LEFT(attribute33, 100), LEFT(attribute34, 100),
    CASE WHEN delete_flag = 'Y' THEN 1 ELSE 0 END,
    created_by,
    creation_date,
    COALESCE(last_updated_by, created_by),
    COALESCE(last_update_date, creation_date),
    tenantid
FROM fdc_document_t_legacy_v33;

DROP INDEX IF EXISTS uk_fdc_document_t_tenant_doc_biz_no;
DROP INDEX IF EXISTS idx_fdc_document_tn1;
DROP INDEX IF EXISTS idx_fdc_document_tn2;

CREATE UNIQUE INDEX uk_fdc_document_t_tenant_doc_biz_no ON fdc_document_t(tenantid, doc_biz_no);
CREATE INDEX idx_fdc_document_tn1 ON fdc_document_t(tenantid);
CREATE INDEX idx_fdc_document_tn2 ON fdc_document_t(arch_barcode);

ALTER TABLE fdc_document_attach_t
    ADD CONSTRAINT fk_doc_att_document
    FOREIGN KEY (document_id) REFERENCES fdc_document_t(doc_id);

ALTER TABLE fdc_arch_storage_t
    ADD CONSTRAINT fk_arch_storage_document
    FOREIGN KEY (document_id) REFERENCES fdc_document_t(doc_id);
