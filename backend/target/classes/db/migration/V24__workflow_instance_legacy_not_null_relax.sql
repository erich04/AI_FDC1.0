-- 兼容 V1 遗留约束：如果旧字段存在，放宽非空约束
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fdc_workflow_instance_t' AND column_name = 'instance_code') THEN
        ALTER TABLE fdc_workflow_instance_t ALTER COLUMN instance_code DROP NOT NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fdc_workflow_instance_t' AND column_name = 'definition_code') THEN
        ALTER TABLE fdc_workflow_instance_t ALTER COLUMN definition_code DROP NOT NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fdc_workflow_instance_t' AND column_name = 'current_node') THEN
        ALTER TABLE fdc_workflow_instance_t ALTER COLUMN current_node DROP NOT NULL;
    END IF;
END $$;

