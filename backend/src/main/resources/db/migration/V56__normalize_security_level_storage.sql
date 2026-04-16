-- 密级统一存字典编码：将历史「中文名称」及小写英文规范为 fdc_security_level_t.security_level_code
-- fdc_arch_t 在仅文档中心/应归档库中可能不存在，需按表存在性跳过，避免迁移失败

UPDATE fdc_document_t d
SET security_level = s.security_level_code
FROM fdc_security_level_t s
WHERE d.security_level IS NOT NULL
  AND btrim(d.security_level) = btrim(s.security_level_name)
  AND coalesce(s.delete_flag, 'N') = 'N';

UPDATE fdc_document_t
SET security_level = upper(security_level)
WHERE security_level IS NOT NULL
  AND security_level ~ '^[a-z]+$';

DO $$
BEGIN
  IF to_regclass('public.fdc_arch_t') IS NOT NULL THEN
    UPDATE fdc_arch_t a
    SET security_level_code = s.security_level_code
    FROM fdc_security_level_t s
    WHERE a.security_level_code IS NOT NULL
      AND btrim(a.security_level_code) = btrim(s.security_level_name)
      AND coalesce(s.delete_flag, 'N') = 'N';

    UPDATE fdc_arch_t
    SET security_level_code = upper(security_level_code)
    WHERE security_level_code IS NOT NULL
      AND security_level_code ~ '^[a-z]+$';
  END IF;
END $$;
