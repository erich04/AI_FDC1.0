-- Rename system user table to tpl_user_t and align schema.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'fdc_user_t'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'tpl_user_t'
    ) THEN
        ALTER TABLE fdc_user_t RENAME TO tpl_user_t;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS tpl_user_t (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(128),
    phone VARCHAR(32),
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    created_by BIGINT DEFAULT 1 NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT DEFAULT 1 NOT NULL,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL
);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'tpl_user_t' AND column_name = 'username'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'tpl_user_t' AND column_name = 'user_name'
    ) THEN
        ALTER TABLE tpl_user_t RENAME COLUMN username TO user_name;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'tpl_user_t' AND column_name = 'real_name'
    ) THEN
        UPDATE tpl_user_t
           SET user_name = real_name
         WHERE (user_name IS NULL OR trim(user_name) = '')
           AND real_name IS NOT NULL;
        ALTER TABLE tpl_user_t DROP COLUMN real_name;
    END IF;
END $$;

ALTER TABLE fdc_user_role_scope_t DROP CONSTRAINT IF EXISTS fk_user_role_scope_user;
ALTER TABLE fdc_user_role_scope_t
    ADD CONSTRAINT fk_user_role_scope_user
    FOREIGN KEY (user_id) REFERENCES tpl_user_t(user_id);
