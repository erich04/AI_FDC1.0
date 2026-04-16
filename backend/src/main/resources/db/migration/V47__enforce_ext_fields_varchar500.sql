-- Enforce extension-field storage type as VARCHAR(500).
-- This migration is intentionally metadata-driven to cover existing schemas safely.

DO $$
DECLARE
    rec RECORD;
BEGIN
    FOR rec IN
        SELECT table_name, column_name
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND (
              (table_name = 'fdc_document_t' AND column_name ~ '^attr([1-9][0-9]?|100)$')
              OR (table_name = 'fdc_application_ext_t' AND column_name ~ '^attr([1-9][0-9]?|[12][0-9][0-9]|300)$')
          )
    LOOP
        EXECUTE format(
            'ALTER TABLE public.%I ALTER COLUMN %I TYPE VARCHAR(500) USING CASE WHEN %I IS NULL THEN NULL ELSE LEFT(%I::text, 500) END',
            rec.table_name,
            rec.column_name,
            rec.column_name,
            rec.column_name
        );
    END LOOP;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'fdc_doc_ext_t'
          AND column_name = 'text_value'
    ) THEN
        ALTER TABLE public.fdc_doc_ext_t
            ALTER COLUMN text_value TYPE VARCHAR(500)
            USING CASE WHEN text_value IS NULL THEN NULL ELSE LEFT(text_value::text, 500) END;
    END IF;
END $$;
