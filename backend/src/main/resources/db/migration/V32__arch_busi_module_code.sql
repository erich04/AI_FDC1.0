-- 档案主表增加业务模块编码（与移交明细 busi_module_code 语义一致）
ALTER TABLE fdc_arch_t ADD COLUMN IF NOT EXISTS busi_module_code VARCHAR(30);
