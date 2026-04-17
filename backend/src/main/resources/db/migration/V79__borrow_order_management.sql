CREATE TABLE IF NOT EXISTS fdc_borrow_order_t (
    borrow_order_id BIGSERIAL PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_name VARCHAR(100) NOT NULL,
    user_department VARCHAR(200),
    applicant_name VARCHAR(100) NOT NULL,
    apply_time TIMESTAMP NOT NULL,
    purpose VARCHAR(100),
    reason TEXT,
    reason_attachment VARCHAR(500),
    demand_approver VARCHAR(100),
    demand_reviewer VARCHAR(100),
    cc_users TEXT,
    status VARCHAR(50) NOT NULL,
    workflow_instance_id VARCHAR(128),
    current_handler VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fdc_borrow_order_detail_t (
    borrow_order_detail_id BIGSERIAL PRIMARY KEY,
    borrow_order_id BIGINT NOT NULL,
    business_code VARCHAR(100),
    document_name VARCHAR(255),
    company VARCHAR(200) NOT NULL,
    document_type VARCHAR(100),
    detail_description TEXT,
    demand_type VARCHAR(100),
    need_return VARCHAR(1) NOT NULL DEFAULT 'N',
    expected_return_date DATE,
    lending_approver VARCHAR(100),
    lending_remark VARCHAR(500),
    handler VARCHAR(100),
    handler_remark VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_borrow_order_detail_order
        FOREIGN KEY (borrow_order_id) REFERENCES fdc_borrow_order_t (borrow_order_id)
);

CREATE TABLE IF NOT EXISTS fdc_borrow_renew_order_t (
    borrow_renew_order_id BIGSERIAL PRIMARY KEY,
    renew_order_no VARCHAR(64) NOT NULL UNIQUE,
    source_order_no VARCHAR(64) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_department VARCHAR(200),
    applicant_name VARCHAR(100) NOT NULL,
    apply_time TIMESTAMP NOT NULL,
    purpose VARCHAR(100),
    reason TEXT,
    reason_attachment VARCHAR(500),
    reviewer VARCHAR(100),
    handler VARCHAR(100),
    cc_users TEXT,
    status VARCHAR(50) NOT NULL,
    workflow_instance_id VARCHAR(128),
    current_handler VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fdc_borrow_renew_detail_t (
    borrow_renew_detail_id BIGSERIAL PRIMARY KEY,
    borrow_renew_order_id BIGINT NOT NULL,
    source_detail_id BIGINT,
    business_code VARCHAR(100),
    document_name VARCHAR(255),
    company VARCHAR(200),
    borrow_type VARCHAR(100),
    borrow_time DATE,
    current_expire_time DATE,
    renew_expire_time DATE,
    renew_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_borrow_renew_detail_order
        FOREIGN KEY (borrow_renew_order_id) REFERENCES fdc_borrow_renew_order_t (borrow_renew_order_id)
);

CREATE INDEX IF NOT EXISTS idx_borrow_order_order_no ON fdc_borrow_order_t(order_no);
CREATE INDEX IF NOT EXISTS idx_borrow_order_status ON fdc_borrow_order_t(status);
CREATE INDEX IF NOT EXISTS idx_borrow_order_detail_order_id ON fdc_borrow_order_detail_t(borrow_order_id);
CREATE INDEX IF NOT EXISTS idx_borrow_renew_order_order_no ON fdc_borrow_renew_order_t(renew_order_no);
CREATE INDEX IF NOT EXISTS idx_borrow_renew_order_source_order_no ON fdc_borrow_renew_order_t(source_order_no);
CREATE INDEX IF NOT EXISTS idx_borrow_renew_detail_order_id ON fdc_borrow_renew_detail_t(borrow_renew_order_id);
