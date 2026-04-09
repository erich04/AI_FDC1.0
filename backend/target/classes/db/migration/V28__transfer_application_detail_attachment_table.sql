CREATE SEQUENCE IF NOT EXISTS fdc_application_detail_attachment_t_attachment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS fdc_application_detail_attachment_t (
    attachment_id BIGINT PRIMARY KEY DEFAULT nextval('fdc_application_detail_attachment_t_attachment_id_seq'),
    application_id BIGINT NOT NULL,
    application_detail_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(1024) NOT NULL,
    mime_type VARCHAR(128),
    file_size BIGINT,
    remark VARCHAR(500),
    delete_flag VARCHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT,
    creation_date TIMESTAMP,
    last_updated_by BIGINT,
    last_update_date TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_fdc_app_detail_att_n1
    ON fdc_application_detail_attachment_t (application_id, application_detail_id, delete_flag);
