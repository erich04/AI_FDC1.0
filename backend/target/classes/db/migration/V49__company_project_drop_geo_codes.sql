DO $$
BEGIN
    IF EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'public'
           AND table_name = 'fdc_company_project_t'
           AND column_name = 'rep_office_code'
    ) THEN
        ALTER TABLE public.fdc_company_project_t
            DROP COLUMN rep_office_code;
    END IF;

    IF EXISTS (
        SELECT 1
          FROM information_schema.columns
         WHERE table_schema = 'public'
           AND table_name = 'fdc_company_project_t'
           AND column_name = 'region_code'
    ) THEN
        ALTER TABLE public.fdc_company_project_t
            DROP COLUMN region_code;
    END IF;
END $$;
