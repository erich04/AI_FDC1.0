-- 正式文档自然键唯一（草稿不算正式文档，不参与约束；已删除行不参与）
-- 自然键：租户 + 公司编码 + 业务模块 + 开始档期 + 文档业务编码
-- 与 uk_fdc_document_t_tenant_doc_biz_no 并存：后者仍约束 tenant内 doc_biz_no 单行；本索引约束四元业务身份不重复。
DO $$
BEGIN
  IF to_regclass('public.fdc_document_t') IS NOT NULL THEN
    EXECUTE $SQL$
      CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_document_t_natural_key_formal
      ON fdc_document_t (
        COALESCE(tenantid, 1),
        company_code,
        biz_module_code,
        start_period,
        doc_biz_no
      )
      WHERE coalesce(delete_flag, 0) = 0
        AND lower(trim(coalesce(lifecycle_status, ''))) <> 'draft'
    $SQL$;
  END IF;
END $$;
