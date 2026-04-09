-- 将 fdc_arch_attachment_t 扩展为通用附件表，支持移交申请行附件。
ALTER TABLE fdc_arch_attachment_t
    ADD COLUMN IF NOT EXISTS biz_domain VARCHAR(32) NOT NULL DEFAULT 'ARCHIVE',
    ADD COLUMN IF NOT EXISTS application_id BIGINT,
    ADD COLUMN IF NOT EXISTS application_detail_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_fdc_arch_attachment_t_biz_domain'
    ) THEN
        ALTER TABLE fdc_arch_attachment_t
            ADD CONSTRAINT ck_fdc_arch_attachment_t_biz_domain
                CHECK (biz_domain IN ('ARCHIVE', 'TRANSFER_APPLICATION'));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_fdc_arch_attachment_tn3
    ON fdc_arch_attachment_t (biz_domain, application_id, application_detail_id, delete_flag);

-- 迁移历史移交申请附件数据到通用附件表
INSERT INTO fdc_arch_attachment_t (
    archive_id,
    session_id,
    attachment_role,
    attachment_type_code,
    attachment_seq,
    version_no,
    file_name,
    file_ext,
    mime_type,
    file_size,
    storage_path,
    storage_key,
    file_hash,
    remark,
    ai_summary,
    ocr_status,
    parse_status,
    vector_status,
    active_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    biz_domain,
    application_id,
    application_detail_id
)
SELECT
    NULL,
    NULL,
    'ELECTRONIC',
    'TRANSFER_DETAIL',
    1,
    1,
    s.file_name,
    NULL,
    s.mime_type,
    s.file_size,
    s.storage_path,
    COALESCE(NULLIF(BTRIM(s.file_name), ''), 'transfer_attachment_' || s.attachment_id::text),
    NULL,
    s.remark,
    NULL,
    'PENDING',
    'PENDING',
    'PENDING',
    'Y',
    s.delete_flag,
    s.created_by,
    s.creation_date,
    s.last_updated_by,
    s.last_update_date,
    'TRANSFER_APPLICATION',
    s.application_id,
    s.application_detail_id
FROM fdc_application_detail_attachment_t s
WHERE NOT EXISTS (
    SELECT 1
    FROM fdc_arch_attachment_t t
    WHERE t.biz_domain = 'TRANSFER_APPLICATION'
      AND t.application_id = s.application_id
      AND t.application_detail_id = s.application_detail_id
      AND t.file_name = s.file_name
      AND t.storage_path = s.storage_path
      AND COALESCE(t.delete_flag, 'N') = COALESCE(s.delete_flag, 'N')
);
