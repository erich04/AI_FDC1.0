-- 回填移交流程发起人名称，修复电子流详情“发起人”为空的存量数据。
UPDATE fdc_workflow_instance_t w
SET initiator_name = '用户-' || a.applicant::text
FROM fdc_application_t a
WHERE w.business_type = 'TRANSFER_APPLICATION'
  AND w.business_key = 'TRN-APP-' || a.application_id::text
  AND COALESCE(BTRIM(w.initiator_name), '') = ''
  AND a.applicant IS NOT NULL;
