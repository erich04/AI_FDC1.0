-- Cleanup legacy backup table created during V39 migration.
-- Execute after confirming data in fdc_document_t is correct.

DROP TABLE IF EXISTS fdc_document_t_legacy_v33;
