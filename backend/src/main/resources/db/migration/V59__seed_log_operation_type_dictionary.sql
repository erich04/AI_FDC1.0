-- 日志操作类型字典：用于前端展示操作类型名称（后端仍存储编码）

INSERT INTO fdc_dict_category_t (
    category_code,
    category_name,
    description,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    tenantid
)
SELECT
    'LOG_OPERATION_TYPE',
    '日志操作类型',
    '操作日志类型字典',
    'Y',
    'N',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    1
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_dict_category_t
    WHERE category_code = 'LOG_OPERATION_TYPE'
      AND coalesce(delete_flag, 'N') = 'N'
);

INSERT INTO fdc_dict_item_t (
    category_code,
    item_code,
    item_name,
    item_value,
    sort_order,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    tenantid
)
SELECT 'LOG_OPERATION_TYPE', 'CREATE', '创建应归档数据', 'CREATE', 1, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_dict_item_t
    WHERE category_code = 'LOG_OPERATION_TYPE'
      AND item_code = 'CREATE'
      AND coalesce(delete_flag, 'N') = 'N'
);

INSERT INTO fdc_dict_item_t (
    category_code,
    item_code,
    item_name,
    item_value,
    sort_order,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    tenantid
)
SELECT 'LOG_OPERATION_TYPE', 'UPDATE', '编辑应归档数据', 'UPDATE', 2, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_dict_item_t
    WHERE category_code = 'LOG_OPERATION_TYPE'
      AND item_code = 'UPDATE'
      AND coalesce(delete_flag, 'N') = 'N'
);

INSERT INTO fdc_dict_item_t (
    category_code,
    item_code,
    item_name,
    item_value,
    sort_order,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    tenantid
)
SELECT 'LOG_OPERATION_TYPE', 'ATTACH_INTEGRATE', '集成电子附件', 'ATTACH_INTEGRATE', 3, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_dict_item_t
    WHERE category_code = 'LOG_OPERATION_TYPE'
      AND item_code = 'ATTACH_INTEGRATE'
      AND coalesce(delete_flag, 'N') = 'N'
);

INSERT INTO fdc_dict_item_t (
    category_code,
    item_code,
    item_name,
    item_value,
    sort_order,
    enable_flag,
    delete_flag,
    created_by,
    creation_date,
    last_updated_by,
    last_update_date,
    tenantid
)
SELECT 'LOG_OPERATION_TYPE', 'DRAFT_SAVE', '保存草稿', 'DRAFT_SAVE', 4, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_dict_item_t
    WHERE category_code = 'LOG_OPERATION_TYPE'
      AND item_code = 'DRAFT_SAVE'
      AND coalesce(delete_flag, 'N') = 'N'
);
