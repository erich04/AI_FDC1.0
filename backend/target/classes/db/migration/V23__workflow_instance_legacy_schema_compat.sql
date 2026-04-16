-- 兼容早期 V1 创建的 wf_workflow_instance 结构（字段如 instance_code/definition_code/started_at）。
-- 现网 WorkflowInstance 实体与 MyBatis 插入依赖下列字段，缺失会导致提交移交时插入失败。

ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS process_instance_id VARCHAR(255);
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS process_definition_id VARCHAR(255);
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS process_definition_key VARCHAR(255);
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS process_definition_name VARCHAR(255);
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS business_id BIGINT;
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS initiator_id VARCHAR(255);
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS initiator_name VARCHAR(255);
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS start_time TIMESTAMP;
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS end_time TIMESTAMP;
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS variables TEXT;
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS created_by BIGINT;
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS creation_date TIMESTAMP;
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS last_updated_by BIGINT;
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS last_update_date TIMESTAMP;

-- 旧字段到新字段的历史数据回填（仅在目标字段和旧字段均存在时回填）
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fdc_workflow_instance_t' AND column_name = 'instance_code') THEN
        UPDATE fdc_workflow_instance_t
        SET process_instance_id = instance_code
        WHERE process_instance_id IS NULL
          AND instance_code IS NOT NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fdc_workflow_instance_t' AND column_name = 'definition_code') THEN
        UPDATE fdc_workflow_instance_t
        SET process_definition_key = definition_code
        WHERE process_definition_key IS NULL
          AND definition_code IS NOT NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fdc_workflow_instance_t' AND column_name = 'started_at') THEN
        UPDATE fdc_workflow_instance_t
        SET start_time = started_at
        WHERE start_time IS NULL
          AND started_at IS NOT NULL;
    END IF;
END $$;

UPDATE fdc_workflow_instance_t
SET creation_date = COALESCE(creation_date, start_time, CURRENT_TIMESTAMP),
    last_update_date = COALESCE(last_update_date, start_time, CURRENT_TIMESTAMP),
    created_by = COALESCE(created_by, 1),
    last_updated_by = COALESCE(last_updated_by, 1),
    initiator_id = COALESCE(initiator_id, 'system'),
    initiator_name = COALESCE(initiator_name, '系统'),
    variables = COALESCE(variables, '{}')
WHERE creation_date IS NULL
   OR last_update_date IS NULL
   OR created_by IS NULL
   OR last_updated_by IS NULL
   OR initiator_id IS NULL
   OR initiator_name IS NULL
   OR variables IS NULL;

CREATE INDEX IF NOT EXISTS idx_fdc_workflow_instance_tn1 ON fdc_workflow_instance_t(process_instance_id);
CREATE INDEX IF NOT EXISTS idx_fdc_workflow_instance_tn2 ON fdc_workflow_instance_t(business_key);

