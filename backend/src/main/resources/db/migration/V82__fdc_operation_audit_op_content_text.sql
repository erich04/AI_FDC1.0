-- Draft saves embed large JSON in audit snapshots; legacy installs may still use a short VARCHAR for op_content.
ALTER TABLE IF EXISTS fdc_operation_audit_log_t
    ALTER COLUMN op_content TYPE TEXT USING op_content::TEXT;
