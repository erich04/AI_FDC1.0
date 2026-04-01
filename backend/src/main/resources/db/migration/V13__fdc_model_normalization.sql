-- FDC physical model normalization — see .docs/FDC_MODEL_MAPPING.md

BEGIN;

-- ========== 1) Rename tables ==========
ALTER TABLE doc_document_type RENAME TO fdc_document_type_t;
ALTER TABLE com_operation_audit RENAME TO fdc_operation_audit_log_t;
ALTER TABLE md_document_organization RENAME TO fdc_document_organization_t;
ALTER TABLE md_document_organization_city RENAME TO fdc_document_organization_city_t;
ALTER TABLE md_country RENAME TO fdc_country_t;
ALTER TABLE md_company_project RENAME TO fdc_company_project_t;
ALTER TABLE md_company_project_line RENAME TO fdc_company_project_line_t;
ALTER TABLE md_company_project_org_category RENAME TO fdc_company_project_org_category_t;
ALTER TABLE md_security_level RENAME TO fdc_security_level_t;
ALTER TABLE md_archive_flow_rule RENAME TO fdc_archive_rule_t;
ALTER TABLE md_dict_category RENAME TO fdc_dict_category_t;
ALTER TABLE md_dict_item RENAME TO fdc_dict_item_t;
ALTER TABLE arc_ext_field_config RENAME TO fdc_doc_field_config_t;
ALTER TABLE arc_archive_create_session RENAME TO fdc_archive_create_session_t;
ALTER TABLE arc_archive RENAME TO fdc_arch_t;
ALTER TABLE arc_archive_ext_value RENAME TO fdc_doc_ext_t;
ALTER TABLE arc_archive_attachment RENAME TO fdc_arch_attachment_t;
ALTER TABLE arc_archive_paper RENAME TO fdc_arch_paper_t;
ALTER TABLE arc_archive_content RENAME TO fdc_arch_content_t;
ALTER TABLE arc_archive_content_chunk RENAME TO fdc_arch_content_chunk_t;
ALTER TABLE arc_archive_chunk_vector RENAME TO fdc_arch_chunk_vector_t;
ALTER TABLE arc_archive_ai_task RENAME TO fdc_arch_ai_task_t;
ALTER TABLE ai_model_config RENAME TO fdc_ai_model_config_t;
ALTER TABLE wh_warehouse RENAME TO fdc_warehouse_t;
ALTER TABLE wh_area RENAME TO fdc_warehouse_area_t;
ALTER TABLE wh_rack RENAME TO fdc_warehouse_rack_t;
ALTER TABLE wh_location RENAME TO fdc_warehouse_location_t;
ALTER TABLE arc_archive_object RENAME TO fdc_archive_object_t;
ALTER TABLE arc_archive_receipt RENAME TO fdc_archive_receipt_t;
ALTER TABLE arc_catalog_task RENAME TO fdc_catalog_task_t;
ALTER TABLE wf_workflow_instance RENAME TO fdc_workflow_instance_t;
ALTER TABLE wf_workflow_task RENAME TO fdc_workflow_task_t;
ALTER TABLE wf_workflow_history RENAME TO fdc_workflow_history_t;
ALTER TABLE arc_borrow_record RENAME TO fdc_borrow_record_t;
ALTER TABLE arc_inventory_task RENAME TO fdc_inventory_task_t;
ALTER TABLE arc_disposal_record RENAME TO fdc_disposal_record_t;
ALTER TABLE arc_bind_batch RENAME TO fdc_bind_batch_t;
ALTER TABLE arc_bind_volume RENAME TO fdc_bind_volume_t;
ALTER TABLE arc_bind_volume_item RENAME TO fdc_bind_volume_item_t;
ALTER TABLE arc_storage_batch RENAME TO fdc_storage_batch_t;
ALTER TABLE arc_storage_batch_item RENAME TO fdc_storage_batch_item_t;
ALTER TABLE arc_storage_ledger RENAME TO fdc_storage_ledger_t;
ALTER TABLE kg_matching_rule_version RENAME TO fdc_kg_matching_rule_version_t;
ALTER TABLE kg_rebuild_task RENAME TO fdc_kg_rebuild_task_t;
ALTER TABLE kg_graph_node RENAME TO fdc_kg_graph_node_t;
ALTER TABLE kg_graph_edge RENAME TO fdc_kg_graph_edge_t;
ALTER TABLE kg_procurement_conversation RENAME TO fdc_procurement_conversation_t;
ALTER TABLE kg_procurement_conversation_message RENAME TO fdc_procurement_conversation_message_t;
ALTER TABLE kg_procurement_context_snapshot RENAME TO fdc_procurement_context_snapshot_t;

-- ========== 2) enabled_flag -> enable_flag ==========
ALTER TABLE fdc_document_type_t RENAME COLUMN enabled_flag TO enable_flag;

ALTER TABLE fdc_document_organization_t RENAME COLUMN enabled_flag TO enable_flag;
ALTER TABLE fdc_document_organization_city_t RENAME COLUMN enabled_flag TO enable_flag;

ALTER TABLE fdc_country_t RENAME COLUMN enabled_flag TO enable_flag;

ALTER TABLE fdc_company_project_t RENAME COLUMN enabled_flag TO enable_flag;
ALTER TABLE fdc_company_project_org_category_t RENAME COLUMN enabled_flag TO enable_flag;

ALTER TABLE fdc_security_level_t RENAME COLUMN enabled_flag TO enable_flag;

DROP INDEX IF EXISTS idx_md_archive_flow_rule_enabled;
ALTER TABLE fdc_archive_rule_t RENAME COLUMN enabled_flag TO enable_flag;
CREATE INDEX IF NOT EXISTS idx_fdc_archive_rule_tn5 ON fdc_archive_rule_t (enable_flag);

ALTER TABLE fdc_dict_category_t DROP CONSTRAINT IF EXISTS ck_md_dict_category_enabled;
ALTER TABLE fdc_dict_category_t RENAME COLUMN enabled_flag TO enable_flag;
ALTER TABLE fdc_dict_category_t ADD CONSTRAINT ck_fdc_dict_category_t_enable CHECK (enable_flag IN ('Y','N'));

ALTER TABLE fdc_dict_item_t DROP CONSTRAINT IF EXISTS ck_md_dict_item_enabled;
DROP INDEX IF EXISTS idx_md_dict_item_category;
ALTER TABLE fdc_dict_item_t RENAME COLUMN enabled_flag TO enable_flag;
CREATE INDEX IF NOT EXISTS idx_fdc_dict_item_tn1 ON fdc_dict_item_t (category_code, enable_flag, delete_flag, sort_order);
ALTER TABLE fdc_dict_item_t ADD CONSTRAINT ck_fdc_dict_item_t_enable CHECK (enable_flag IN ('Y','N'));

ALTER TABLE fdc_doc_field_config_t DROP CONSTRAINT IF EXISTS ck_arc_ext_field_config_enabled;
DROP INDEX IF EXISTS idx_arc_ext_field_config_type;
DROP INDEX IF EXISTS idx_arc_ext_field_config_query;
DROP INDEX IF EXISTS idx_arc_ext_field_config_semantic;
ALTER TABLE fdc_doc_field_config_t RENAME COLUMN enabled_flag TO enable_flag;
ALTER TABLE fdc_doc_field_config_t ADD CONSTRAINT ck_fdc_doc_field_config_t_enable CHECK (enable_flag IN ('Y', 'N'));
CREATE INDEX IF NOT EXISTS idx_fdc_doc_field_config_tn1 ON fdc_doc_field_config_t (document_type_code, enable_flag, delete_flag, form_sort_order);
CREATE INDEX IF NOT EXISTS idx_fdc_doc_field_config_tn2 ON fdc_doc_field_config_t (document_type_code, query_enabled_flag, delete_flag, query_sort_order);
CREATE INDEX IF NOT EXISTS idx_fdc_doc_field_config_tn3 ON fdc_doc_field_config_t (semantic_code, document_type_code, delete_flag);

ALTER TABLE fdc_ai_model_config_t DROP CONSTRAINT IF EXISTS ck_ai_model_config_enabled;
ALTER TABLE fdc_ai_model_config_t RENAME COLUMN enabled_flag TO enable_flag;
ALTER TABLE fdc_ai_model_config_t ADD CONSTRAINT ck_fdc_ai_model_config_t_enable CHECK (enable_flag IN ('Y', 'N'));

ALTER TABLE fdc_kg_matching_rule_version_t DROP CONSTRAINT IF EXISTS ck_kg_matching_rule_enabled;
ALTER TABLE fdc_kg_matching_rule_version_t RENAME COLUMN enabled_flag TO enable_flag;
ALTER TABLE fdc_kg_matching_rule_version_t ADD CONSTRAINT ck_fdc_kg_matching_rule_version_t_enable CHECK (enable_flag IN ('Y', 'N'));

-- ========== 3) Semantic PK + sequences (tables without cross-FK on PK renames) ==========

ALTER TABLE fdc_document_type_t RENAME COLUMN id TO document_type_id;
ALTER SEQUENCE doc_document_type_id_seq RENAME TO fdc_document_type_t_document_type_id_seq;

ALTER TABLE fdc_operation_audit_log_t RENAME COLUMN id TO operation_audit_log_id;
ALTER SEQUENCE com_operation_audit_id_seq RENAME TO fdc_operation_audit_log_t_operation_audit_log_id_seq;

ALTER TABLE fdc_document_organization_t RENAME COLUMN id TO document_organization_id;
ALTER SEQUENCE md_document_organization_id_seq RENAME TO fdc_document_organization_t_document_organization_id_seq;

ALTER TABLE fdc_document_organization_city_t RENAME COLUMN id TO document_organization_city_id;
ALTER SEQUENCE md_document_organization_city_id_seq RENAME TO fdc_document_organization_city_t_document_organization_city_id_seq;

ALTER TABLE fdc_country_t RENAME COLUMN id TO country_id;
ALTER SEQUENCE md_country_id_seq RENAME TO fdc_country_t_country_id_seq;

ALTER TABLE fdc_company_project_t RENAME COLUMN id TO company_project_id;
ALTER SEQUENCE md_company_project_id_seq RENAME TO fdc_company_project_t_company_project_id_seq;

ALTER TABLE fdc_company_project_line_t RENAME COLUMN id TO company_project_line_id;
ALTER SEQUENCE md_company_project_line_id_seq RENAME TO fdc_company_project_line_t_company_project_line_id_seq;

ALTER TABLE fdc_company_project_org_category_t RENAME COLUMN id TO company_project_org_category_id;
ALTER SEQUENCE md_company_project_org_category_id_seq RENAME TO fdc_company_project_org_category_t_company_project_org_category_id_seq;

ALTER TABLE fdc_security_level_t RENAME COLUMN id TO security_level_id;
ALTER SEQUENCE md_security_level_id_seq RENAME TO fdc_security_level_t_security_level_id_seq;

ALTER TABLE fdc_archive_rule_t RENAME COLUMN id TO archive_rule_id;
ALTER SEQUENCE md_archive_flow_rule_id_seq RENAME TO fdc_archive_rule_t_archive_rule_id_seq;

ALTER TABLE fdc_dict_category_t RENAME COLUMN id TO dict_category_id;
ALTER SEQUENCE md_dict_category_id_seq RENAME TO fdc_dict_category_t_dict_category_id_seq;

ALTER TABLE fdc_dict_item_t RENAME COLUMN id TO dict_item_id;
ALTER SEQUENCE md_dict_item_id_seq RENAME TO fdc_dict_item_t_dict_item_id_seq;

ALTER TABLE fdc_doc_field_config_t RENAME COLUMN field_id TO doc_field_config_id;
ALTER SEQUENCE arc_ext_field_config_field_id_seq RENAME TO fdc_doc_field_config_t_doc_field_config_id_seq;

ALTER TABLE fdc_doc_ext_t RENAME COLUMN value_id TO doc_ext_id;
ALTER SEQUENCE arc_archive_ext_value_value_id_seq RENAME TO fdc_doc_ext_t_doc_ext_id_seq;

ALTER TABLE fdc_ai_model_config_t RENAME COLUMN model_config_id TO ai_model_config_id;
ALTER SEQUENCE ai_model_config_model_config_id_seq RENAME TO fdc_ai_model_config_t_ai_model_config_id_seq;

ALTER TABLE fdc_warehouse_t RENAME COLUMN id TO warehouse_id;
ALTER SEQUENCE wh_warehouse_id_seq RENAME TO fdc_warehouse_t_warehouse_id_seq;

ALTER TABLE fdc_warehouse_area_t RENAME COLUMN id TO warehouse_area_id;
ALTER SEQUENCE wh_area_id_seq RENAME TO fdc_warehouse_area_t_warehouse_area_id_seq;

ALTER TABLE fdc_warehouse_rack_t RENAME COLUMN id TO warehouse_rack_id;
ALTER SEQUENCE wh_rack_id_seq RENAME TO fdc_warehouse_rack_t_warehouse_rack_id_seq;

ALTER TABLE fdc_warehouse_location_t RENAME COLUMN id TO warehouse_location_id;
ALTER SEQUENCE wh_location_id_seq RENAME TO fdc_warehouse_location_t_warehouse_location_id_seq;

ALTER TABLE fdc_archive_object_t RENAME COLUMN id TO archive_object_id;
ALTER SEQUENCE arc_archive_object_id_seq RENAME TO fdc_archive_object_t_archive_object_id_seq;

ALTER TABLE fdc_archive_receipt_t RENAME COLUMN id TO archive_receipt_id;
ALTER SEQUENCE arc_archive_receipt_id_seq RENAME TO fdc_archive_receipt_t_archive_receipt_id_seq;

ALTER TABLE fdc_catalog_task_t RENAME COLUMN id TO catalog_task_id;
ALTER SEQUENCE arc_catalog_task_id_seq RENAME TO fdc_catalog_task_t_catalog_task_id_seq;

ALTER TABLE fdc_workflow_instance_t RENAME COLUMN id TO workflow_instance_id;
ALTER SEQUENCE wf_workflow_instance_id_seq RENAME TO fdc_workflow_instance_t_workflow_instance_id_seq;

ALTER TABLE fdc_workflow_task_t RENAME COLUMN id TO workflow_task_id;
ALTER SEQUENCE wf_workflow_task_id_seq RENAME TO fdc_workflow_task_t_workflow_task_id_seq;

ALTER TABLE fdc_workflow_history_t RENAME COLUMN id TO workflow_history_id;
ALTER SEQUENCE wf_workflow_history_id_seq RENAME TO fdc_workflow_history_t_workflow_history_id_seq;

ALTER TABLE fdc_borrow_record_t RENAME COLUMN id TO borrow_record_id;
ALTER SEQUENCE arc_borrow_record_id_seq RENAME TO fdc_borrow_record_t_borrow_record_id_seq;

ALTER TABLE fdc_inventory_task_t RENAME COLUMN id TO inventory_task_id;
ALTER SEQUENCE arc_inventory_task_id_seq RENAME TO fdc_inventory_task_t_inventory_task_id_seq;

ALTER TABLE fdc_disposal_record_t RENAME COLUMN id TO disposal_record_id;
ALTER SEQUENCE arc_disposal_record_id_seq RENAME TO fdc_disposal_record_t_disposal_record_id_seq;

-- ========== 4) Legacy soft-delete ==========
ALTER TABLE fdc_archive_object_t ADD COLUMN IF NOT EXISTS delete_flag CHAR(1) NOT NULL DEFAULT 'N';
UPDATE fdc_archive_object_t SET delete_flag = CASE WHEN deleted IS NOT NULL AND deleted <> 0 THEN 'Y' ELSE 'N' END;
ALTER TABLE fdc_archive_object_t DROP COLUMN IF EXISTS deleted;

ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS delete_flag CHAR(1) NOT NULL DEFAULT 'N';
UPDATE fdc_warehouse_location_t SET delete_flag = CASE WHEN deleted IS NOT NULL AND deleted <> 0 THEN 'Y' ELSE 'N' END;
ALTER TABLE fdc_warehouse_location_t DROP COLUMN IF EXISTS deleted;

-- ========== 5) warehouse_location: time column names ==========
ALTER TABLE fdc_warehouse_location_t RENAME COLUMN updated_at TO last_update_date;

-- ========== 6) utilization_rate precision ==========
ALTER TABLE fdc_warehouse_location_t
    ALTER COLUMN utilization_rate TYPE NUMERIC(7, 6)
    USING CASE WHEN utilization_rate IS NULL THEN NULL ELSE utilization_rate::NUMERIC(7, 6) END;

-- ========== 7) Governance columns ==========

ALTER TABLE fdc_document_type_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_document_type_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_document_type_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_document_type_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_operation_audit_log_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_operation_audit_log_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_operation_audit_log_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_operation_audit_log_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_document_organization_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_document_organization_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_document_organization_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_document_organization_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_document_organization_city_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_document_organization_city_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_document_organization_city_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_document_organization_city_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_country_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_country_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_country_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_country_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_company_project_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_company_project_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_company_project_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_company_project_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_company_project_line_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_company_project_line_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_company_project_line_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_company_project_line_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_company_project_org_category_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_company_project_org_category_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_company_project_org_category_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_company_project_org_category_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_security_level_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_security_level_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_security_level_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_security_level_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_archive_rule_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_archive_rule_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_archive_rule_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_archive_rule_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_dict_category_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_dict_category_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_dict_category_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_dict_category_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_dict_item_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_dict_item_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_dict_item_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_dict_item_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_doc_field_config_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_doc_field_config_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_doc_field_config_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_doc_field_config_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_arch_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_arch_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_arch_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_arch_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_doc_ext_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_doc_ext_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_doc_ext_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_doc_ext_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_arch_attachment_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_arch_attachment_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_arch_attachment_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_arch_attachment_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_ai_model_config_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_ai_model_config_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_ai_model_config_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_ai_model_config_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE fdc_warehouse_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_warehouse_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_warehouse_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_warehouse_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE fdc_warehouse_t ADD COLUMN IF NOT EXISTS enable_flag CHAR(1) NOT NULL DEFAULT 'Y';
ALTER TABLE fdc_warehouse_t ADD COLUMN IF NOT EXISTS delete_flag CHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE fdc_warehouse_area_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_warehouse_area_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_warehouse_area_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_warehouse_area_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE fdc_warehouse_area_t ADD COLUMN IF NOT EXISTS enable_flag CHAR(1) NOT NULL DEFAULT 'Y';
ALTER TABLE fdc_warehouse_area_t ADD COLUMN IF NOT EXISTS delete_flag CHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE fdc_warehouse_rack_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_warehouse_rack_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_warehouse_rack_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_warehouse_rack_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE fdc_warehouse_rack_t ADD COLUMN IF NOT EXISTS enable_flag CHAR(1) NOT NULL DEFAULT 'Y';
ALTER TABLE fdc_warehouse_rack_t ADD COLUMN IF NOT EXISTS delete_flag CHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS tenantid BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS sys_description VARCHAR(500);
ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS last_update_trace_id VARCHAR(100);
ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS last_update_version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS enable_flag CHAR(1) NOT NULL DEFAULT 'Y';
ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS created_by BIGINT NOT NULL DEFAULT 1;
ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE fdc_warehouse_location_t ADD COLUMN IF NOT EXISTS last_updated_by BIGINT NOT NULL DEFAULT 1;

UPDATE fdc_warehouse_location_t SET creation_date = COALESCE(creation_date, last_update_date, CURRENT_TIMESTAMP),
    last_update_date = COALESCE(last_update_date, CURRENT_TIMESTAMP);

COMMIT;
