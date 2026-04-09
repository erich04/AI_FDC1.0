-- 将移交申请明细的开始/结束档期调整为 yyyy-MM 文本格式，
-- 与发起归档模块 begin_period/end_period 保持一致，并修复存量数据。
ALTER TABLE fdc_application_detail_t
    ALTER COLUMN start_arch_period TYPE VARCHAR(7) USING (
        CASE
            WHEN start_arch_period IS NULL THEN NULL
            ELSE TO_CHAR(start_arch_period, 'YYYY-MM')
        END
    );

ALTER TABLE fdc_application_detail_t
    ALTER COLUMN end_arch_period TYPE VARCHAR(7) USING (
        CASE
            WHEN end_arch_period IS NULL THEN NULL
            ELSE TO_CHAR(end_arch_period, 'YYYY-MM')
        END
    );
