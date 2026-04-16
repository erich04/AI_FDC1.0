DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'fdc_arch_t'
    ) THEN
        ALTER TABLE public.fdc_arch_t
            DROP COLUMN IF EXISTS archive_filing_code CASCADE;
    END IF;
END $$;
