BEGIN;

ALTER TABLE fdc_document_type_t
    RENAME COLUMN document_type_id TO busi_module_id;

ALTER TABLE fdc_doc_field_config_t
    RENAME COLUMN document_type_code TO busi_module_code;

ALTER INDEX IF EXISTS idx_fdc_doc_field_config_tn1
    RENAME TO idx_fdc_doc_field_config_t_busi_module_form;
ALTER INDEX IF EXISTS idx_fdc_doc_field_config_tn2
    RENAME TO idx_fdc_doc_field_config_t_busi_module_query;
ALTER INDEX IF EXISTS idx_fdc_doc_field_config_tn3
    RENAME TO idx_fdc_doc_field_config_t_semantic_busi_module;

COMMIT;
