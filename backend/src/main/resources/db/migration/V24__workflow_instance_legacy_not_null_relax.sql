-- 兼容 V1 遗留约束：旧版 wf_workflow_instance 要求 instance_code/definition_code/current_node 非空，
-- 但当前工作流写入模型使用 process_* 字段，不再写这些旧字段。
-- 若不放宽约束，会在移交提交时插入 fdc_workflow_instance_t 失败。

ALTER TABLE fdc_workflow_instance_t ALTER COLUMN instance_code DROP NOT NULL;
ALTER TABLE fdc_workflow_instance_t ALTER COLUMN definition_code DROP NOT NULL;
ALTER TABLE fdc_workflow_instance_t ALTER COLUMN current_node DROP NOT NULL;

