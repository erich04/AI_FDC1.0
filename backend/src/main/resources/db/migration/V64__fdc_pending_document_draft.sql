-- 应归档「草稿」独立表：仅存用户保存的 JSON 载荷，不占用 fdc_document_t（正式文档表）。
-- 正式提交时由应用写入 fdc_document_t 并软删除本表记录。

CREATE TABLE IF NOT EXISTS fdc_pending_document_draft_t (
    draft_id              BIGSERIAL PRIMARY KEY,
    tenantid              BIGINT DEFAULT 1,
    payload_json          JSONB NOT NULL DEFAULT '{}'::jsonb,
    delete_flag           SMALLINT NOT NULL DEFAULT 0,
    created_by            BIGINT NOT NULL,
    creation_date         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by       BIGINT NOT NULL,
    last_update_date      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sys_description       VARCHAR(500),
    last_update_trace_id  VARCHAR(100),
    last_update_version   INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_fdc_pending_document_draft_created_by
    ON fdc_pending_document_draft_t (created_by)
    WHERE coalesce(delete_flag, 0) = 0;

COMMENT ON TABLE fdc_pending_document_draft_t IS '应归档数据草稿（与 fdc_document_t 正式行分离）';
