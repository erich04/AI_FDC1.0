INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_VCH', '会计凭证', NULL, 'Y', 'FIN_ACC', 2, 'FIN_ACC', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_VCH' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_BOOK', '账簿账表', NULL, 'Y', 'FIN_ACC', 2, 'FIN_ACC', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_BOOK' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_RPT', '财务报表', NULL, 'Y', 'FIN_ACC', 2, 'FIN_ACC', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_RPT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_CLOSE', '结账与对账', NULL, 'Y', 'FIN_ACC', 2, 'FIN_ACC', 4, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_CLOSE' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_VCH_AP', '应付凭证', NULL, 'Y', 'FIN_ACC_VCH', 3, 'FIN_ACC/FIN_ACC_VCH', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_VCH' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_VCH_AP' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_VCH_AR', '应收凭证', NULL, 'Y', 'FIN_ACC_VCH', 3, 'FIN_ACC/FIN_ACC_VCH', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_VCH' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_VCH_AR' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_VCH_GL', '总账凭证', NULL, 'Y', 'FIN_ACC_VCH', 3, 'FIN_ACC/FIN_ACC_VCH', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_VCH' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_VCH_GL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_BOOK_GL', '总账', NULL, 'Y', 'FIN_ACC_BOOK', 3, 'FIN_ACC/FIN_ACC_BOOK', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_BOOK' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_BOOK_GL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_BOOK_SUB', '明细账', NULL, 'Y', 'FIN_ACC_BOOK', 3, 'FIN_ACC/FIN_ACC_BOOK', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_BOOK' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_BOOK_SUB' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_BOOK_AUX', '辅助账', NULL, 'Y', 'FIN_ACC_BOOK', 3, 'FIN_ACC/FIN_ACC_BOOK', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_BOOK' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_BOOK_AUX' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_RPT_BS', '资产负债表', NULL, 'Y', 'FIN_ACC_RPT', 3, 'FIN_ACC/FIN_ACC_RPT', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_RPT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_RPT_BS' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_RPT_IS', '利润表', NULL, 'Y', 'FIN_ACC_RPT', 3, 'FIN_ACC/FIN_ACC_RPT', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_RPT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_RPT_IS' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_RPT_CF', '现金流量表', NULL, 'Y', 'FIN_ACC_RPT', 3, 'FIN_ACC/FIN_ACC_RPT', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_RPT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_RPT_CF' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_CLOSE_RECON', '对账单', NULL, 'Y', 'FIN_ACC_CLOSE', 3, 'FIN_ACC/FIN_ACC_CLOSE', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_CLOSE' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_CLOSE_RECON' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_ACC_CLOSE_ADJ', '调整分录', NULL, 'Y', 'FIN_ACC_CLOSE', 3, 'FIN_ACC/FIN_ACC_CLOSE', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_CLOSE' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_ACC_CLOSE_ADJ' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_FILING', '税务申报', NULL, 'Y', 'FIN_TAX', 2, 'FIN_TAX', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_FILING' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_INV', '发票与税控', NULL, 'Y', 'FIN_TAX', 2, 'FIN_TAX', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_INV' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_REPORT', '税务报表与台账', NULL, 'Y', 'FIN_TAX', 2, 'FIN_TAX', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_REPORT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_POLICY', '税务政策与函件', NULL, 'Y', 'FIN_TAX', 2, 'FIN_TAX', 4, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_POLICY' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_FILING_VAT', '增值税申报', NULL, 'Y', 'FIN_TAX_FILING', 3, 'FIN_TAX/FIN_TAX_FILING', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_FILING' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_FILING_VAT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_FILING_CIT', '企业所得税申报', NULL, 'Y', 'FIN_TAX_FILING', 3, 'FIN_TAX/FIN_TAX_FILING', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_FILING' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_FILING_CIT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_FILING_IIT', '个人所得税申报', NULL, 'Y', 'FIN_TAX_FILING', 3, 'FIN_TAX/FIN_TAX_FILING', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_FILING' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_FILING_IIT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_INV_OUT', '销项发票', NULL, 'Y', 'FIN_TAX_INV', 3, 'FIN_TAX/FIN_TAX_INV', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_INV' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_INV_OUT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_INV_IN', '进项发票', NULL, 'Y', 'FIN_TAX_INV', 3, 'FIN_TAX/FIN_TAX_INV', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_INV' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_INV_IN' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_INV_TAXCTRL', '税控资料', NULL, 'Y', 'FIN_TAX_INV', 3, 'FIN_TAX/FIN_TAX_INV', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_INV' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_INV_TAXCTRL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_REPORT_VATLEDGER', '增值税台账', NULL, 'Y', 'FIN_TAX_REPORT', 3, 'FIN_TAX/FIN_TAX_REPORT', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_REPORT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_REPORT_VATLEDGER' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_REPORT_TAXRISK', '税务风险分析', NULL, 'Y', 'FIN_TAX_REPORT', 3, 'FIN_TAX/FIN_TAX_REPORT', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_REPORT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_REPORT_TAXRISK' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_POLICY_NOTICE', '税局通知与函件', NULL, 'Y', 'FIN_TAX_POLICY', 3, 'FIN_TAX/FIN_TAX_POLICY', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_POLICY' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_POLICY_NOTICE' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_TAX_POLICY_POLICY', '政策解读', NULL, 'Y', 'FIN_TAX_POLICY', 3, 'FIN_TAX/FIN_TAX_POLICY', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_POLICY' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_TAX_POLICY_POLICY' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_PAYMENT', '资金收付', NULL, 'Y', 'FIN_FUND', 2, 'FIN_FUND', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_PAYMENT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_BANK', '银行单据', NULL, 'Y', 'FIN_FUND', 2, 'FIN_FUND', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_BANK' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_FINANCE', '融资与授信', NULL, 'Y', 'FIN_FUND', 2, 'FIN_FUND', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_FINANCE' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_TREASURY', '资金管理', NULL, 'Y', 'FIN_FUND', 2, 'FIN_FUND', 4, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_TREASURY' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_PAYMENT_PAY', '付款申请与指令', NULL, 'Y', 'FIN_FUND_PAYMENT', 3, 'FIN_FUND/FIN_FUND_PAYMENT', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_PAYMENT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_PAYMENT_PAY' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_PAYMENT_REC', '收款回单与确认', NULL, 'Y', 'FIN_FUND_PAYMENT', 3, 'FIN_FUND/FIN_FUND_PAYMENT', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_PAYMENT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_PAYMENT_REC' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_BANK_STMT', '银行对账单', NULL, 'Y', 'FIN_FUND_BANK', 3, 'FIN_FUND/FIN_FUND_BANK', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_BANK' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_BANK_STMT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_BANK_SLIP', '银行回单', NULL, 'Y', 'FIN_FUND_BANK', 3, 'FIN_FUND/FIN_FUND_BANK', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_BANK' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_BANK_SLIP' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_BANK_DAILY', '资金日报', NULL, 'Y', 'FIN_FUND_BANK', 3, 'FIN_FUND/FIN_FUND_BANK', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_BANK' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_BANK_DAILY' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_FINANCE_LOAN', '借款与贷款协议', NULL, 'Y', 'FIN_FUND_FINANCE', 3, 'FIN_FUND/FIN_FUND_FINANCE', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_FINANCE' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_FINANCE_LOAN' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_FINANCE_CREDIT', '授信与保函资料', NULL, 'Y', 'FIN_FUND_FINANCE', 3, 'FIN_FUND/FIN_FUND_FINANCE', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_FINANCE' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_FINANCE_CREDIT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_TREASURY_FOREX', '外汇结算与汇兑', NULL, 'Y', 'FIN_FUND_TREASURY', 3, 'FIN_FUND/FIN_FUND_TREASURY', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_TREASURY' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_TREASURY_FOREX' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_FUND_TREASURY_POOL', '资金池与集中收付', NULL, 'Y', 'FIN_FUND_TREASURY', 3, 'FIN_FUND/FIN_FUND_TREASURY', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_TREASURY' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_FUND_TREASURY_POOL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_BUDGET', '预算与经营计划', NULL, 'Y', 'FIN_OTHER', 2, 'FIN_OTHER', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_BUDGET' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_COST', '成本费用管理', NULL, 'Y', 'FIN_OTHER', 2, 'FIN_OTHER', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_COST' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_ASSET', '资产管理', NULL, 'Y', 'FIN_OTHER', 2, 'FIN_OTHER', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_ASSET' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_AUDIT', '审计与内控', NULL, 'Y', 'FIN_OTHER', 2, 'FIN_OTHER', 4, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_AUDIT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_BUDGET_ANNUAL', '年度预算', NULL, 'Y', 'FIN_OTHER_BUDGET', 3, 'FIN_OTHER/FIN_OTHER_BUDGET', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_BUDGET' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_BUDGET_ANNUAL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_BUDGET_ROLL', '滚动预测', NULL, 'Y', 'FIN_OTHER_BUDGET', 3, 'FIN_OTHER/FIN_OTHER_BUDGET', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_BUDGET' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_BUDGET_ROLL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_COST_EXP', '报销单据', NULL, 'Y', 'FIN_OTHER_COST', 3, 'FIN_OTHER/FIN_OTHER_COST', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_COST' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_COST_EXP' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_COST_SETTLE', '成本结算', NULL, 'Y', 'FIN_OTHER_COST', 3, 'FIN_OTHER/FIN_OTHER_COST', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_COST' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_COST_SETTLE' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_ASSET_FA', '固定资产台账', NULL, 'Y', 'FIN_OTHER_ASSET', 3, 'FIN_OTHER/FIN_OTHER_ASSET', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_ASSET' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_ASSET_FA' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_ASSET_INV', '存货盘点与出入库', NULL, 'Y', 'FIN_OTHER_ASSET', 3, 'FIN_OTHER/FIN_OTHER_ASSET', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_ASSET' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_ASSET_INV' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_AUDIT_INTERNAL', '内部审计', NULL, 'Y', 'FIN_OTHER_AUDIT', 3, 'FIN_OTHER/FIN_OTHER_AUDIT', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_AUDIT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_AUDIT_INTERNAL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'FIN_OTHER_AUDIT_IC', '内控资料', NULL, 'Y', 'FIN_OTHER_AUDIT', 3, 'FIN_OTHER/FIN_OTHER_AUDIT', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_AUDIT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'FIN_OTHER_AUDIT_IC' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_HR', '人力资源', NULL, 'Y', 'NON_FIN', 2, 'NON_FIN', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_HR' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_LEGAL', '法务合规', NULL, 'Y', 'NON_FIN', 2, 'NON_FIN', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_LEGAL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_PROC', '采购供应链', NULL, 'Y', 'NON_FIN', 2, 'NON_FIN', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROC' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_PROJECT', '项目与运营', NULL, 'Y', 'NON_FIN', 2, 'NON_FIN', 4, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROJECT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_IT', '信息化', NULL, 'Y', 'NON_FIN', 2, 'NON_FIN', 5, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_IT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_HR_CONTRACT', '劳动合同与协议', NULL, 'Y', 'NON_FIN_HR', 3, 'NON_FIN/NON_FIN_HR', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_HR' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_HR_CONTRACT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_HR_PAYROLL', '薪酬福利资料', NULL, 'Y', 'NON_FIN_HR', 3, 'NON_FIN/NON_FIN_HR', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_HR' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_HR_PAYROLL' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_HR_TRAIN', '培训记录', NULL, 'Y', 'NON_FIN_HR', 3, 'NON_FIN/NON_FIN_HR', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_HR' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_HR_TRAIN' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_LEGAL_CONTRACT', '合同文本', NULL, 'Y', 'NON_FIN_LEGAL', 3, 'NON_FIN/NON_FIN_LEGAL', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_LEGAL' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_LEGAL_CONTRACT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_LEGAL_CASE', '诉讼与仲裁', NULL, 'Y', 'NON_FIN_LEGAL', 3, 'NON_FIN/NON_FIN_LEGAL', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_LEGAL' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_LEGAL_CASE' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_LEGAL_COMPLIANCE', '合规审查', NULL, 'Y', 'NON_FIN_LEGAL', 3, 'NON_FIN/NON_FIN_LEGAL', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_LEGAL' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_LEGAL_COMPLIANCE' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_PROC_PO', '采购订单与协议', NULL, 'Y', 'NON_FIN_PROC', 3, 'NON_FIN/NON_FIN_PROC', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROC' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROC_PO' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_PROC_VENDOR', '供应商资料', NULL, 'Y', 'NON_FIN_PROC', 3, 'NON_FIN/NON_FIN_PROC', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROC' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROC_VENDOR' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_PROC_DELIVERY', '验收与交付', NULL, 'Y', 'NON_FIN_PROC', 3, 'NON_FIN/NON_FIN_PROC', 3, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROC' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROC_DELIVERY' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_PROJECT_PLAN', '项目计划与里程碑', NULL, 'Y', 'NON_FIN_PROJECT', 3, 'NON_FIN/NON_FIN_PROJECT', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROJECT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROJECT_PLAN' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_PROJECT_REPORT', '项目报告与纪要', NULL, 'Y', 'NON_FIN_PROJECT', 3, 'NON_FIN/NON_FIN_PROJECT', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROJECT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_PROJECT_REPORT' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_IT_CHANGE', '变更与上线文档', NULL, 'Y', 'NON_FIN_IT', 3, 'NON_FIN/NON_FIN_IT', 1, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_IT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_IT_CHANGE' AND delete_flag = 'N');

INSERT INTO fdc_document_type_t (type_code, type_name, description, enable_flag, parent_code, level_num, ancestor_path, sort_order, delete_flag, created_by, creation_date, last_updated_by, last_update_date)
SELECT 'NON_FIN_IT_ASSET', 'IT资产台账', NULL, 'Y', 'NON_FIN_IT', 3, 'NON_FIN/NON_FIN_IT', 2, 'N', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_IT' AND delete_flag = 'N')
  AND NOT EXISTS (SELECT 1 FROM fdc_document_type_t WHERE type_code = 'NON_FIN_IT_ASSET' AND delete_flag = 'N');

