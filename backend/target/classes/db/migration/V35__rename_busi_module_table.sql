DO $$
BEGIN
    IF to_regclass('public.fdc_document_type_t') IS NOT NULL
       AND to_regclass('public.fdc_busi_module_t') IS NULL THEN
        ALTER TABLE fdc_document_type_t RENAME TO fdc_busi_module_t;
    END IF;
END $$;
