-- 兼容历史库结构：部分环境 fdc_arch_t.document_type_code 仍为 varchar(7)，
-- 无法保存如 FUND_DOC 等长度 > 7 的文档类型编码。
ALTER TABLE fdc_arch_t
    ALTER COLUMN document_type_code TYPE VARCHAR(64);
