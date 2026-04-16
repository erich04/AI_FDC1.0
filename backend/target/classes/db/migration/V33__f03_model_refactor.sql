-- F03 文档查询模型重构

-- 1. 清理旧表 (如果存在)
DROP TABLE IF EXISTS fdc_doc_log_att_t CASCADE;
DROP TABLE IF EXISTS fdc_doc_op_log_t CASCADE;
DROP TABLE IF EXISTS fdc_audit_log_t CASCADE;
DROP TABLE IF EXISTS fdc_doc_att_t CASCADE;
DROP TABLE IF EXISTS fdc_document_attach_t CASCADE;
DROP TABLE IF EXISTS fdc_arch_storage_t CASCADE;
DROP TABLE IF EXISTS fdc_arch_t CASCADE;
DROP TABLE IF EXISTS fdc_doc_t CASCADE;
DROP TABLE IF EXISTS fdc_document_t CASCADE;
DROP TABLE IF EXISTS fdc_file_t CASCADE;

-- 2. 创建文件元数据表
CREATE TABLE fdc_file_t (
    file_id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(50),
    source_system VARCHAR(60),
    storage_platform VARCHAR(60) NOT NULL,
    file_md5 VARCHAR(64),
    enable_flag CHAR(1) DEFAULT 'Y' NOT NULL,
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 3. 创建合并后的文档表 (包含 100 个扩展字段)
CREATE TABLE fdc_document_t (
    document_id BIGSERIAL PRIMARY KEY,
    tenantid BIGINT NOT NULL,
    doc_busi_no VARCHAR(60) NOT NULL,
    doc_name VARCHAR(500) NOT NULL,
    document_type_id BIGINT NOT NULL,
    archived_entity_unit_id BIGINT NOT NULL,
    business_module_id BIGINT NOT NULL,
    owner_id BIGINT,
    doc_status VARCHAR(30) NOT NULL,
    archive_barcode VARCHAR(60),
    signer BIGINT,
    sign_time TIMESTAMP,
    write_off_by BIGINT,
    write_off_time TIMESTAMP,
    volume_id BIGINT,
    
    -- 扩展字段 1-100
    attribute1 VARCHAR(500), attribute2 VARCHAR(500), attribute3 VARCHAR(500), attribute4 VARCHAR(500), attribute5 VARCHAR(500),
    attribute6 VARCHAR(500), attribute7 VARCHAR(500), attribute8 VARCHAR(500), attribute9 VARCHAR(500), attribute10 VARCHAR(500),
    attribute11 VARCHAR(500), attribute12 VARCHAR(500), attribute13 VARCHAR(500), attribute14 VARCHAR(500), attribute15 VARCHAR(500),
    attribute16 VARCHAR(500), attribute17 VARCHAR(500), attribute18 VARCHAR(500), attribute19 VARCHAR(500), attribute20 VARCHAR(500),
    attribute21 VARCHAR(500), attribute22 VARCHAR(500), attribute23 VARCHAR(500), attribute24 VARCHAR(500), attribute25 VARCHAR(500),
    attribute26 VARCHAR(500), attribute27 VARCHAR(500), attribute28 VARCHAR(500), attribute29 VARCHAR(500), attribute30 VARCHAR(500),
    attribute31 VARCHAR(500), attribute32 VARCHAR(500), attribute33 VARCHAR(500), attribute34 VARCHAR(500), attribute35 VARCHAR(500),
    attribute36 VARCHAR(500), attribute37 VARCHAR(500), attribute38 VARCHAR(500), attribute39 VARCHAR(500), attribute40 VARCHAR(500),
    attribute41 VARCHAR(500), attribute42 VARCHAR(500), attribute43 VARCHAR(500), attribute44 VARCHAR(500), attribute45 VARCHAR(500),
    attribute46 VARCHAR(500), attribute47 VARCHAR(500), attribute48 VARCHAR(500), attribute49 VARCHAR(500), attribute50 VARCHAR(500),
    attribute51 VARCHAR(500), attribute52 VARCHAR(500), attribute53 VARCHAR(500), attribute54 VARCHAR(500), attribute55 VARCHAR(500),
    attribute56 VARCHAR(500), attribute57 VARCHAR(500), attribute58 VARCHAR(500), attribute59 VARCHAR(500), attribute60 VARCHAR(500),
    attribute61 VARCHAR(500), attribute62 VARCHAR(500), attribute63 VARCHAR(500), attribute64 VARCHAR(500), attribute65 VARCHAR(500),
    attribute66 VARCHAR(500), attribute67 VARCHAR(500), attribute68 VARCHAR(500), attribute69 VARCHAR(500), attribute70 VARCHAR(500),
    attribute71 VARCHAR(500), attribute72 VARCHAR(500), attribute73 VARCHAR(500), attribute74 VARCHAR(500), attribute75 VARCHAR(500),
    attribute76 VARCHAR(500), attribute77 VARCHAR(500), attribute78 VARCHAR(500), attribute79 VARCHAR(500), attribute80 VARCHAR(500),
    attribute81 VARCHAR(500), attribute82 VARCHAR(500), attribute83 VARCHAR(500), attribute84 VARCHAR(500), attribute85 VARCHAR(500),
    attribute86 VARCHAR(500), attribute87 VARCHAR(500), attribute88 VARCHAR(500), attribute89 VARCHAR(500), attribute90 VARCHAR(500),
    attribute91 VARCHAR(500), attribute92 VARCHAR(500), attribute93 VARCHAR(500), attribute94 VARCHAR(500), attribute95 VARCHAR(500),
    attribute96 VARCHAR(500), attribute97 VARCHAR(500), attribute98 VARCHAR(500), attribute99 VARCHAR(500), attribute100 VARCHAR(500),

    enable_flag CHAR(1) DEFAULT 'Y' NOT NULL,
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_update_version INTEGER DEFAULT 0 NOT NULL,
    CONSTRAINT uk_fdc_document_t UNIQUE (tenantid, doc_busi_no)
);

CREATE INDEX idx_fdc_document_tn1 ON fdc_document_t(tenantid);
CREATE INDEX idx_fdc_document_tn2 ON fdc_document_t(archive_barcode);

-- 4. 创建文档附件关联表
CREATE TABLE fdc_document_attach_t (
    document_attach_id BIGSERIAL PRIMARY KEY,
    tenantid BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    file_id BIGINT NOT NULL,
    attach_category VARCHAR(30),
    att_type VARCHAR(30),
    enable_flag CHAR(1) DEFAULT 'Y' NOT NULL,
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_update_version INTEGER DEFAULT 0 NOT NULL,
    CONSTRAINT fk_doc_att_document FOREIGN KEY (document_id) REFERENCES fdc_document_t(document_id),
    CONSTRAINT fk_doc_att_file FOREIGN KEY (file_id) REFERENCES fdc_file_t(file_id)
);

CREATE INDEX idx_fdc_document_attach_tn1 ON fdc_document_attach_t(tenantid);

-- 5. 创建业务操作审计日志表
CREATE TABLE fdc_audit_log_t (
    audit_log_id BIGSERIAL PRIMARY KEY,
    tenantid BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    object_type VARCHAR(30) NOT NULL,
    operated_by BIGINT NOT NULL,
    operation_type VARCHAR(30) NOT NULL,
    op_content VARCHAR(500) NOT NULL,
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_update_version INTEGER DEFAULT 0 NOT NULL
);

CREATE INDEX idx_fdc_audit_log_tn1 ON fdc_audit_log_t(tenantid);
CREATE INDEX idx_fdc_audit_log_n2 ON fdc_audit_log_t(object_id, object_type);

-- 6. 创建档案物理信息表
CREATE TABLE fdc_arch_storage_t (
    arch_storage_id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    volume_id BIGINT,
    volume_seq_no VARCHAR(60),
    binder BIGINT,
    bind_time TIMESTAMP,
    storage_person BIGINT,
    storage_time TIMESTAMP,
    enable_flag CHAR(1) DEFAULT 'Y' NOT NULL,
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL,
    created_by BIGINT NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_arch_storage_document FOREIGN KEY (document_id) REFERENCES fdc_document_t(document_id)
);
