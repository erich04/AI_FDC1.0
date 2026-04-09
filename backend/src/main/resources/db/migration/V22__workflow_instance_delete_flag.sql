-- 部分环境因 V1 已创建 wf_workflow_instance，V10 的 CREATE IF NOT EXISTS 被跳过，表上无 delete_flag。
-- MyBatis-Plus WorkflowInstance 使用 @TableLogic(delete_flag)，必须存在该列。
ALTER TABLE fdc_workflow_instance_t ADD COLUMN IF NOT EXISTS delete_flag VARCHAR(1) NOT NULL DEFAULT 'N';

COMMENT ON COLUMN fdc_workflow_instance_t.delete_flag IS '逻辑删除 Y/N，与实体 @TableLogic 一致';
