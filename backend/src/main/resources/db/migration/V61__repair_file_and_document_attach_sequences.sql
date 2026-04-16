-- 防止历史手工写入 ID 后序列落后，导致上传附件主键冲突
DO $$
DECLARE
  v_seq text;
BEGIN
  IF to_regclass('public.fdc_file_t') IS NOT NULL THEN
    v_seq := pg_get_serial_sequence('fdc_file_t', 'file_id');
    IF v_seq IS NOT NULL THEN
      EXECUTE format(
        'SELECT setval(%L, GREATEST(coalesce((SELECT max(file_id) FROM fdc_file_t), 0), 1), true)',
        v_seq
      );
    END IF;
  END IF;

  IF to_regclass('public.fdc_document_attach_t') IS NOT NULL THEN
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'public' AND table_name = 'fdc_document_attach_t' AND column_name = 'document_attach_id'
    ) THEN
      v_seq := pg_get_serial_sequence('fdc_document_attach_t', 'document_attach_id');
      IF v_seq IS NOT NULL THEN
        EXECUTE format(
          'SELECT setval(%L, GREATEST(coalesce((SELECT max(document_attach_id) FROM fdc_document_attach_t), 0), 1), true)',
          v_seq
        );
      END IF;
    ELSIF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'public' AND table_name = 'fdc_document_attach_t' AND column_name = 'attach_id'
    ) THEN
      v_seq := pg_get_serial_sequence('fdc_document_attach_t', 'attach_id');
      IF v_seq IS NOT NULL THEN
        EXECUTE format(
          'SELECT setval(%L, GREATEST(coalesce((SELECT max(attach_id) FROM fdc_document_attach_t), 0), 1), true)',
          v_seq
        );
      END IF;
    END IF;
  END IF;
END $$;
