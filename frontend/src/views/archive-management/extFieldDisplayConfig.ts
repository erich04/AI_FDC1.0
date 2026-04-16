export type ExtFieldShowFor =
  | 'ALL'
  | 'ACCOUNTING_OR_TAX'
  | 'ACCOUNTING'
  | 'FUND'
  | 'FUND_OR_NON_FIN'
  | 'TAX_OR_FUND'
  | 'NON_FIN'

export const hardCodedExtLabelMap: Record<string, string> = {
  country: '国家',
  repOffice: '代表处',
  region: '地区部',
  companyTag: '公司标签',
  invoiceNo: '发票号',
  refNo: '其他相关编号',
  accountant: '会计',
  scannedBy: '扫描员',
  issueDateRange: '开立日期',
  maturityDateRange: '到期日',
  lgExpiryDateRange: '保函失效日期',
  lgLedgerStatus: '保函台账状态',
  bankName: '银行名称',
  currency: '币种',
  amount: '金额',
  issuingAuthority: '签发机构',
  disposalTimeRange: '报废时间',
  businessVolumeNo: '业务册号',
  lgWorkflowNo: '保函电子流编号',
  lgNo: '保函编号'
}

export const hardCodedFieldShowFor: Record<string, ExtFieldShowFor> = {
  country: 'ALL',
  repOffice: 'ALL',
  region: 'ALL',
  companyTag: 'ALL',
  invoiceNo: 'ACCOUNTING_OR_TAX',
  refNo: 'ALL',
  accountant: 'ACCOUNTING',
  scannedBy: 'ACCOUNTING',
  issueDateRange: 'FUND',
  maturityDateRange: 'FUND_OR_NON_FIN',
  lgExpiryDateRange: 'FUND',
  lgLedgerStatus: 'FUND',
  bankName: 'FUND',
  currency: 'TAX_OR_FUND',
  amount: 'TAX_OR_FUND',
  issuingAuthority: 'NON_FIN',
  disposalTimeRange: 'ACCOUNTING',
  businessVolumeNo: 'ACCOUNTING',
  lgWorkflowNo: 'FUND',
  lgNo: 'FUND'
}

/** F03：国家/地域 + 扩展 41–56（与 F03_01_DocumentSearch 筛选栏顺序一致） */
export const EXT_DETAIL_FIELD_ORDER: string[] = [
  'country',
  'repOffice',
  'region',
  'companyTag',
  'invoiceNo',
  'refNo',
  'accountant',
  'scannedBy',
  'issueDateRange',
  'maturityDateRange',
  'lgExpiryDateRange',
  'lgLedgerStatus',
  'bankName',
  'currency',
  'amount',
  'issuingAuthority',
  'disposalTimeRange',
  'businessVolumeNo',
  'lgWorkflowNo',
  'lgNo'
]

export const isHardCodedFieldVisible = (fieldKey: string, documentTypeName: string) => {
  const showFor = hardCodedFieldShowFor[fieldKey]
  if (!showFor) return true
  const isAccounting = documentTypeName.includes('会计')
  const isTax = documentTypeName.includes('税务')
  const isFund = documentTypeName.includes('资金')
  const isNonFin = documentTypeName.includes('非财经')
  switch (showFor) {
    case 'ACCOUNTING_OR_TAX':
      return isAccounting || isTax
    case 'ACCOUNTING':
      return isAccounting
    case 'FUND':
      return isFund
    case 'FUND_OR_NON_FIN':
      return isFund || isNonFin
    case 'TAX_OR_FUND':
      return isTax || isFund
    case 'NON_FIN':
      return isNonFin
    default:
      return true
  }
}
