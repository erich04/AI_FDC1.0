-- 日志操作类型补充：批量创建、批量更新

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
SELECT 'LOG_OPERATION_TYPE', 'BATCH_CREATE', '批量创建应归档数据', 'BATCH_CREATE', 5, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_dict_item_t
    WHERE category_code = 'LOG_OPERATION_TYPE'
      AND item_code = 'BATCH_CREATE'
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
SELECT 'LOG_OPERATION_TYPE', 'BATCH_UPDATE', '批量更新应归档数据', 'BATCH_UPDATE', 6, 'Y', 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_dict_item_t
    WHERE category_code = 'LOG_OPERATION_TYPE'
      AND item_code = 'BATCH_UPDATE'
      AND coalesce(delete_flag, 'N') = 'N'
);
