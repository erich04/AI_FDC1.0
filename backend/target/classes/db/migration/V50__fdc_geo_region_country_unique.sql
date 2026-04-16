-- One active row per country_code: enforce at DB level (aligns with company.country_code -> single rep office + region).
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
          FROM information_schema.tables
         WHERE table_schema = 'public'
           AND table_name = 'fdc_geo_region_t'
    ) THEN
        -- Soft-delete duplicate active rows per country; keep the row with smallest geo_region_id.
        UPDATE public.fdc_geo_region_t g
           SET delete_flag = 'Y',
               last_update_date = CURRENT_TIMESTAMP
         WHERE g.delete_flag = 'N'
           AND g.geo_region_id > (
               SELECT MIN(g2.geo_region_id)
                 FROM public.fdc_geo_region_t g2
                WHERE g2.country_code = g.country_code
                  AND g2.delete_flag = 'N'
           );

        CREATE UNIQUE INDEX IF NOT EXISTS uk_fdc_geo_region_t_country_active
            ON public.fdc_geo_region_t (country_code)
            WHERE delete_flag = 'N';
    END IF;
END $$;
