DO $$
BEGIN
IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'fdc_document_type_t'
      AND column_name = 'type_code'
) THEN
UPDATE fdc_document_type_t
SET delete_flag = 'Y',
    last_updated_by = 1,
    last_update_date = CURRENT_TIMESTAMP
WHERE delete_flag = 'N'
  AND type_code IN ('DOC', 'DOC_FIN', 'DOC_FIN_VCH', 'DOC_FIN_VCH_PAY', 'DOC_FIN_VCH_PAY_DOM', 'DOC_CON');

INSERT INTO fdc_document_type_t (
    type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order,
    delete_flag, created_by, creation_date, last_updated_by, last_update_date
)
SELECT 'FIN_ACC', '会计文档', NULL, 'Y', NULL, 1, NULL, 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (
    type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order,
    delete_flag, created_by, creation_date, last_updated_by, last_update_date
)
SELECT 'FIN_TAX', '税务文档', NULL, 'Y', NULL, 1, NULL, 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (
    type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order,
    delete_flag, created_by, creation_date, last_updated_by, last_update_date
)
SELECT 'FIN_FUND', '资金文档', NULL, 'Y', NULL, 1, NULL, 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (
    type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order,
    delete_flag, created_by, creation_date, last_updated_by, last_update_date
)
SELECT 'FIN_OTHER', '财经其他文档', NULL, 'Y', NULL, 1, NULL, 4, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (
    type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order,
    delete_flag, created_by, creation_date, last_updated_by, last_update_date
)
SELECT 'NON_FIN', '非财经文档', NULL, 'Y', NULL, 1, NULL, 5, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN' AND delete_flag = 'N');

END IF;
END $$;
