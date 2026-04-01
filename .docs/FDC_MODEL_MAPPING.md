# 旧表名 → fdc_*_T 映射（V13 迁移）

实施依据：[01_DataModel.md](01_DataModel.md) 命名与强制通用字段。

| 旧表名 | 新表名 |
|--------|--------|
| doc_document_type | fdc_document_type_t |
| com_operation_audit | fdc_operation_audit_log_t |
| md_document_organization | fdc_document_organization_t |
| md_document_organization_city | fdc_document_organization_city_t |
| md_country | fdc_country_t |
| md_company_project | fdc_company_project_t |
| md_company_project_line | fdc_company_project_line_t |
| md_company_project_org_category | fdc_company_project_org_category_t |
| md_security_level | fdc_security_level_t |
| md_archive_flow_rule | fdc_archive_rule_t |
| md_dict_category | fdc_dict_category_t |
| md_dict_item | fdc_dict_item_t |
| arc_ext_field_config | fdc_doc_field_config_t |
| arc_archive_create_session | fdc_archive_create_session_t |
| arc_archive | fdc_arch_t |
| arc_archive_ext_value | fdc_doc_ext_t |
| arc_archive_attachment | fdc_arch_attachment_t |
| arc_archive_paper | fdc_arch_paper_t |
| arc_archive_content | fdc_arch_content_t |
| arc_archive_content_chunk | fdc_arch_content_chunk_t |
| arc_archive_chunk_vector | fdc_arch_chunk_vector_t |
| arc_archive_ai_task | fdc_arch_ai_task_t |
| ai_model_config | fdc_ai_model_config_t |
| wh_warehouse | fdc_warehouse_t |
| wh_area | fdc_warehouse_area_t |
| wh_rack | fdc_warehouse_rack_t |
| wh_location | fdc_warehouse_location_t |
| arc_archive_object | fdc_archive_object_t |
| arc_archive_receipt | fdc_archive_receipt_t |
| arc_catalog_task | fdc_catalog_task_t |
| wf_workflow_instance | fdc_workflow_instance_t |
| wf_workflow_task | fdc_workflow_task_t |
| wf_workflow_history | fdc_workflow_history_t |
| arc_borrow_record | fdc_borrow_record_t |
| arc_inventory_task | fdc_inventory_task_t |
| arc_disposal_record | fdc_disposal_record_t |
| arc_bind_batch | fdc_bind_batch_t |
| arc_bind_volume | fdc_bind_volume_t |
| arc_bind_volume_item | fdc_bind_volume_item_t |
| arc_storage_batch | fdc_storage_batch_t |
| arc_storage_batch_item | fdc_storage_batch_item_t |
| arc_storage_ledger | fdc_storage_ledger_t |
| kg_matching_rule_version | fdc_kg_matching_rule_version_t |
| kg_rebuild_task | fdc_kg_rebuild_task_t |
| kg_graph_node | fdc_kg_graph_node_t |
| kg_graph_edge | fdc_kg_graph_edge_t |
| kg_procurement_conversation | fdc_procurement_conversation_t |
| kg_procurement_conversation_message | fdc_procurement_conversation_message_t |
| kg_procurement_context_snapshot | fdc_procurement_context_snapshot_t |

## 列级变更（V13）

- `enabled_flag` → `enable_flag`（含 CHECK 约束与索引表达式重绑）
- 强制通用字段：`tenantid`、`sys_description`、`last_update_trace_id`（缺省列则 `ADD`，`tenantid` 默认 `1`）
- 主键列 `id` → 语义化 `*_id`（与序列名 `fdc_*_*_id_seq` 对齐）
- `arc_archive_object.deleted` / `wh_location.deleted` → `delete_flag` CHAR(1)（0/1 映射为 N/Y）
- `created_at`/`updated_at` → `creation_date`/`last_update_date`（与规范一致的表）

## 外键依赖

迁移仅 `ALTER TABLE ... RENAME TO`；PostgreSQL 保留外键引用关系。子表列名未改时无需重建 FK。
