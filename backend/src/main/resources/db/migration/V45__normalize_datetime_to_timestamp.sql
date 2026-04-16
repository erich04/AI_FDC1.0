DO $$
BEGIN
    -- Keep period fields as DATE/YearMonth semantics; normalize other date/time fields to TIMESTAMP.
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'fdc_arch_t'
          AND column_name = 'document_date'
          AND data_type = 'date'
    ) THEN
        ALTER TABLE public.fdc_arch_t
            ALTER COLUMN document_date TYPE TIMESTAMP
            USING document_date::timestamp;
    END IF;
END $$;
