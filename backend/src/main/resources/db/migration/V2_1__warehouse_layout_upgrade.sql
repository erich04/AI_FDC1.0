-- 兼容旧库：部分环境在 V1 被 baseline 标记后并未真实创建 wh_* 表
DO $$
BEGIN
    IF to_regclass('public.wh_warehouse') IS NOT NULL THEN
        EXECUTE 'ALTER TABLE wh_warehouse ADD COLUMN IF NOT EXISTS area_size NUMERIC(12, 2)';
        EXECUTE 'ALTER TABLE wh_warehouse ADD COLUMN IF NOT EXISTS photo_url VARCHAR(255)';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.wh_area') IS NOT NULL THEN
        EXECUTE 'ALTER TABLE wh_area ADD COLUMN IF NOT EXISTS start_x INTEGER DEFAULT 0';
        EXECUTE 'ALTER TABLE wh_area ADD COLUMN IF NOT EXISTS start_y INTEGER DEFAULT 0';
        EXECUTE 'ALTER TABLE wh_area ADD COLUMN IF NOT EXISTS width INTEGER DEFAULT 620';
        EXECUTE 'ALTER TABLE wh_area ADD COLUMN IF NOT EXISTS height INTEGER DEFAULT 260';
        EXECUTE 'ALTER TABLE wh_area ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.wh_rack') IS NOT NULL THEN
        EXECUTE 'ALTER TABLE wh_rack ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.wh_warehouse') IS NOT NULL THEN
        EXECUTE '
            UPDATE wh_warehouse
            SET area_size = COALESCE(area_size, 260.00),
                updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP)
            WHERE area_size IS NULL OR updated_at IS NULL
        ';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.wh_area') IS NOT NULL THEN
        EXECUTE '
            UPDATE wh_area
            SET start_x = COALESCE(start_x, 0),
                start_y = COALESCE(start_y, 0),
                width = COALESCE(width, 620),
                height = COALESCE(height, 260),
                updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP)
            WHERE start_x IS NULL OR start_y IS NULL OR width IS NULL OR height IS NULL OR updated_at IS NULL
        ';
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('public.wh_rack') IS NOT NULL THEN
        EXECUTE '
            UPDATE wh_rack
            SET updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP)
            WHERE updated_at IS NULL
        ';
    END IF;
END $$;
