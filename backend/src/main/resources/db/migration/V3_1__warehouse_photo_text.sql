-- 兼容旧库：若 V1 被 baseline 标记但未真实建表，先补齐最小 wh_* 结构
CREATE TABLE IF NOT EXISTS wh_warehouse (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(64) NOT NULL UNIQUE,
    warehouse_name VARCHAR(128) NOT NULL,
    warehouse_type VARCHAR(64) NOT NULL,
    manager_name VARCHAR(64) NOT NULL,
    contact_phone VARCHAR(32),
    address VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    area_size NUMERIC(12, 2),
    photo_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS wh_area (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(64) NOT NULL,
    area_code VARCHAR(64) NOT NULL,
    area_name VARCHAR(128) NOT NULL,
    sort_order INTEGER DEFAULT 1,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_x INTEGER DEFAULT 0,
    start_y INTEGER DEFAULT 0,
    width INTEGER DEFAULT 620,
    height INTEGER DEFAULT 260,
    UNIQUE (warehouse_code, area_code)
);

CREATE TABLE IF NOT EXISTS wh_rack (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(64) NOT NULL,
    area_code VARCHAR(64) NOT NULL,
    rack_code VARCHAR(64) NOT NULL,
    rack_name VARCHAR(128) NOT NULL,
    layer_count INTEGER NOT NULL,
    slot_count INTEGER NOT NULL,
    start_x INTEGER DEFAULT 40,
    start_y INTEGER DEFAULT 40,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (warehouse_code, rack_code)
);

ALTER TABLE wh_warehouse
    ALTER COLUMN photo_url TYPE TEXT;
