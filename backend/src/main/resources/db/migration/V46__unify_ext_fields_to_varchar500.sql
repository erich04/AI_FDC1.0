-- Unify all extension fields to VARCHAR(500).
-- Scope:
-- 1) fdc_document_t.attr1..attr100
-- 2) fdc_application_ext_t.attr1..attr300
-- 3) fdc_doc_ext_t.text_value

DO $$
DECLARE
    i INTEGER;
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'fdc_document_t'
    ) THEN
        FOR i IN 1..100 LOOP
            EXECUTE format(
                'ALTER TABLE public.fdc_document_t ALTER COLUMN attr%s TYPE VARCHAR(500) USING CASE WHEN attr%s IS NULL THEN NULL ELSE LEFT(attr%s::text, 500) END',
                i, i, i
            );
        END LOOP;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'fdc_application_ext_t'
    ) THEN
        FOR i IN 1..300 LOOP
            EXECUTE format(
                'ALTER TABLE public.fdc_application_ext_t ALTER COLUMN attr%s TYPE VARCHAR(500) USING CASE WHEN attr%s IS NULL THEN NULL ELSE LEFT(attr%s::text, 500) END',
                i, i, i
            );
        END LOOP;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'fdc_doc_ext_t'
    ) THEN
        ALTER TABLE public.fdc_doc_ext_t
            ALTER COLUMN text_value TYPE VARCHAR(500)
            USING CASE WHEN text_value IS NULL THEN NULL ELSE LEFT(text_value::text, 500) END;
    END IF;
END $$;
