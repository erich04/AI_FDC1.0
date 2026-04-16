-- 防止历史手工写入 ID 后序列落后，导致上传附件主键冲突
SELECT setval(
  pg_get_serial_sequence('fdc_file_t', 'file_id'),
  GREATEST(coalesce((SELECT max(file_id) FROM fdc_file_t), 0), 1),
  true
);

SELECT setval(
  pg_get_serial_sequence('fdc_document_attach_t', 'document_attach_id'),
  GREATEST(coalesce((SELECT max(document_attach_id) FROM fdc_document_attach_t), 0), 1),
  true
);
