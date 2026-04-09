package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.archiveflow.domain.ArchiveFlowRule;
import com.smartarchive.archiveflow.domain.SecurityLevelDictionary;
import com.smartarchive.archiveflow.mapper.ArchiveFlowLookupMapper;
import com.smartarchive.archiveflow.mapper.ArchiveFlowRuleMapper;
import com.smartarchive.archiveflow.mapper.SecurityLevelDictionaryMapper;
import com.smartarchive.archivemanage.domain.ArchiveExtFieldConfig;
import com.smartarchive.archivemanage.domain.ArchiveExtValue;
import com.smartarchive.archivemanage.domain.ArchivePaper;
import com.smartarchive.archivemanage.domain.ArchiveRecord;
import com.smartarchive.archivemanage.domain.TransferApplication;
import com.smartarchive.archivemanage.domain.TransferApplicationDetail;
import com.smartarchive.archivemanage.dto.ArchiveDefaultResolveResponse;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.mapper.ArchiveExtFieldConfigMapper;
import com.smartarchive.archivemanage.mapper.ArchiveExtValueMapper;
import com.smartarchive.archivemanage.mapper.ArchivePaperMapper;
import com.smartarchive.archivemanage.mapper.ArchiveRecordMapper;
import com.smartarchive.archivemanage.mapper.TransferApplicationDetailMapper;
import com.smartarchive.archivemanage.mapper.TransferApplicationExtMapper;
import com.smartarchive.archivemanage.mapper.TransferApplicationMapper;
import com.smartarchive.archivemanage.service.DocumentTypeExtFieldService;
import com.smartarchive.archivemanage.service.TransferApplicationArchiveMaterializationService;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.companyproject.domain.CompanyProject;
import com.smartarchive.companyproject.mapper.CompanyProjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TransferApplicationArchiveMaterializationServiceImpl implements TransferApplicationArchiveMaterializationService {

    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final Pattern ATTR_COLUMN_PATTERN = Pattern.compile("^attr([1-9][0-9]{0,2})$", Pattern.CASE_INSENSITIVE);

    private final TransferApplicationMapper transferApplicationMapper;
    private final TransferApplicationDetailMapper transferApplicationDetailMapper;
    private final TransferApplicationExtMapper transferApplicationExtMapper;
    private final ArchiveRecordMapper archiveRecordMapper;
    private final ArchiveExtValueMapper archiveExtValueMapper;
    private final ArchivePaperMapper archivePaperMapper;
    private final ArchiveFlowRuleMapper archiveFlowRuleMapper;
    private final DocumentTypeExtFieldService documentTypeExtFieldService;
    private final ArchiveExtFieldConfigMapper archiveExtFieldConfigMapper;
    private final CompanyProjectMapper companyProjectMapper;
    private final SecurityLevelDictionaryMapper securityLevelDictionaryMapper;
    private final ArchiveFlowLookupMapper archiveFlowLookupMapper;

    @Override
    @Transactional
    public void materializeAfterApproval(Long applicationId) {
        TransferApplication application = requireApplication(applicationId);
        if ("Y".equalsIgnoreCase(trimToEmpty(application.getArchivesMaterialized()))) {
            return;
        }
        List<TransferApplicationDetail> details = transferApplicationDetailMapper.selectList(
            new LambdaQueryWrapper<TransferApplicationDetail>()
                .eq(TransferApplicationDetail::getApplicationId, applicationId)
                .eq(TransferApplicationDetail::getDeleteFlag, "N")
                .orderByAsc(TransferApplicationDetail::getApplicationDetailId));
        if (details.isEmpty()) {
            throw new BusinessException("移交申请无有效明细，无法写入档案");
        }
        String documentTypeCode = application.getDocumentTypeCode();
        List<DocumentTypeExtFieldResponse> effectiveFields = documentTypeExtFieldService.listEffective(documentTypeCode);
        List<Map<String, Object>> extRows =
            transferApplicationExtMapper.selectByMasterId(applicationId, application.getTenantid());
        Map<Long, Map<String, String>> extByDetailId = decodeExtRows(extRows, documentTypeCode);

        for (TransferApplicationDetail detail : details) {
            insertArchiveForDetail(application, detail, extByDetailId.getOrDefault(detail.getApplicationDetailId(), Map.of()), effectiveFields);
        }

        application.setArchivesMaterialized("Y");
        application.setApplicationStatus("APPROVED");
        application.setStatus("APPROVED");
        application.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        application.setLastUpdateDate(LocalDateTime.now());
        transferApplicationMapper.updateById(application);
    }

    @Override
    @Transactional
    public void markRejected(Long applicationId) {
        TransferApplication application = transferApplicationMapper.selectOne(new LambdaQueryWrapper<TransferApplication>()
            .eq(TransferApplication::getApplicationId, applicationId)
            .eq(TransferApplication::getDeleteFlag, "N")
            .last("limit 1"));
        if (application == null) {
            return;
        }
        application.setApplicationStatus("REJECTED");
        application.setStatus("REJECTED");
        application.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        application.setLastUpdateDate(LocalDateTime.now());
        transferApplicationMapper.updateById(application);
    }

    private TransferApplication requireApplication(Long applicationId) {
        TransferApplication application = transferApplicationMapper.selectOne(new LambdaQueryWrapper<TransferApplication>()
            .eq(TransferApplication::getApplicationId, applicationId)
            .eq(TransferApplication::getDeleteFlag, "N")
            .last("limit 1"));
        if (application == null) {
            throw new BusinessException("移交申请不存在");
        }
        return application;
    }

    private void insertArchiveForDetail(TransferApplication application,
                                        TransferApplicationDetail detail,
                                        Map<String, String> extValues,
                                        List<DocumentTypeExtFieldResponse> effectiveFields) {
        String businessCode = trimToNull(detail.getDocBusiNo());
        if (StringUtils.hasText(businessCode)) {
            LambdaUpdateWrapper<ArchiveRecord> updateWrapper = new LambdaUpdateWrapper<ArchiveRecord>()
                .eq(ArchiveRecord::getBusinessCode, businessCode)
                .eq(ArchiveRecord::getDeleteFlag, "N")
                .set(ArchiveRecord::getArchiveStatus, "TRANSFERRED")
                .set(ArchiveRecord::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
                .set(ArchiveRecord::getLastUpdateDate, LocalDateTime.now());
            int affected = archiveRecordMapper.update(null, updateWrapper);
            if (affected > 0) {
                return;
            }
        }

        CompanyProject project = requireCompanyProject(detail.getCompanyProjectCode());
        ArchiveDefaultResolveResponse defaults = resolveDefaultsForDetail(
            detail.getCompanyProjectCode(),
            application.getDocumentTypeCode(),
            trimToNull(detail.getBusiModuleCode()),
            trimToNull(detail.getArchPlaceAlpha2Code()));

        String securityLevelCode = firstNonBlank(defaults.getSecurityLevelCode(), firstSecurityLevelCode());
        String documentOrganizationCode = firstNonBlank(defaults.getDocumentOrganizationCode(), firstDocumentOrganizationCode());
        Integer retentionYears = defaults.getRetentionPeriodYears() != null ? defaults.getRetentionPeriodYears() : 10;
        String countryCode = firstNonBlank(defaults.getCountryCode(), project.getCountryCode());

        LocalDate docDate = detail.getDocGenerationDate() != null ? detail.getDocGenerationDate()
            : (application.getApplicationDate() != null ? application.getApplicationDate().toLocalDate() : LocalDate.now());
        String beginPeriod = toYearMonthPeriod(detail.getStartArchPeriod(), docDate);
        String endPeriod = toYearMonthPeriod(detail.getEndArchPeriod(), docDate);
        String carrierType = resolveCarrierType(firstNonBlank(detail.getCarrierType(), application.getCarrierType()));
        String archiveTypeCode = firstNonBlank(detail.getArchTypeCode(), application.getDocumentTypeCode());
        String dutyPerson = application.getApplicant() != null ? "用户-" + application.getApplicant() : "—";
        String dutyDepartment = firstNonBlank(application.getDepartment(), "—");
        String remark = buildRemark(application, detail);

        ArchiveRecord archive = new ArchiveRecord();
        archive.setArchiveCode(generateCode("ARC"));
        archive.setArchiveFilingCode(generateCode("FILE"));
        archive.setCreateMode("MANUAL");
        archive.setArchiveStatus("TRANSFERRED");
        archive.setDocumentTypeCode(application.getDocumentTypeCode());
        archive.setCompanyProjectCode(detail.getCompanyProjectCode().trim());
        archive.setBeginPeriod(beginPeriod);
        archive.setEndPeriod(endPeriod);
        archive.setBusinessCode(businessCode);
        archive.setDocumentName(firstNonBlank(detail.getDocName(), "未命名文档"));
        archive.setDutyPerson(dutyPerson);
        archive.setDutyDepartment(dutyDepartment);
        archive.setDocumentDate(docDate);
        archive.setSecurityLevelCode(securityLevelCode);
        archive.setSourceSystem("TRANSFER_APPLICATION:" + application.getApplicationNumber());
        archive.setArchiveDestination(firstNonBlank(defaults.getArchiveDestination(), trimToNull(detail.getArchPlaceAlpha2Code())));
        archive.setOriginPlace(trimToNull(detail.getArchPlaceAlpha2Code()));
        archive.setCarrierTypeCode(carrierType);
        archive.setRemark(remark);
        archive.setAiArchiveSummary(trimToNull(detail.getDescription()));
        archive.setDocumentOrganizationCode(documentOrganizationCode);
        archive.setRetentionPeriodYears(retentionYears);
        archive.setArchiveTypeCode(archiveTypeCode);
        archive.setCountryCode(countryCode);
        archive.setParseStatus("FAILED");
        archive.setVectorStatus("FAILED");
        archive.setQaIndexStatus("FAILED");
        archive.setSessionId(null);
        archive.setDeleteFlag("N");
        archive.setCreatedBy(SYSTEM_OPERATOR_ID);
        archive.setCreationDate(LocalDateTime.now());
        archive.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        archive.setLastUpdateDate(LocalDateTime.now());
        archiveRecordMapper.insert(archive);
        persistArchiveExtValues(archive.getArchiveId(), effectiveFields, extValues);

        if ("PAPER".equals(carrierType) || "HYBRID".equals(carrierType)) {
            int copies = detail.getArchCopies() == null ? 1 : Math.max(1, detail.getArchCopies().intValue());
            ArchivePaper paper = new ArchivePaper();
            paper.setArchiveId(archive.getArchiveId());
            paper.setPlannedCopyCount(copies);
            paper.setActualCopyCount(copies);
            paper.setRemark(trimToNull(detail.getRemark()));
            paper.setCreatedBy(SYSTEM_OPERATOR_ID);
            paper.setCreationDate(LocalDateTime.now());
            paper.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            paper.setLastUpdateDate(LocalDateTime.now());
            archivePaperMapper.insert(paper);
        }
    }

    private Map<Long, Map<String, String>> decodeExtRows(List<Map<String, Object>> extRows, String documentTypeCode) {
        if (extRows == null || extRows.isEmpty()) {
            return Map.of();
        }
        List<ArchiveExtFieldConfig> configs = archiveExtFieldConfigMapper.selectList(new LambdaQueryWrapper<ArchiveExtFieldConfig>()
            .eq(ArchiveExtFieldConfig::getDocumentTypeCode, documentTypeCode)
            .eq(ArchiveExtFieldConfig::getDeleteFlag, "N")
            .eq(ArchiveExtFieldConfig::getEnabledFlag, "Y"));
        Map<String, String> columnToFieldCode = new HashMap<>();
        for (ArchiveExtFieldConfig cfg : configs) {
            String columnName = trimToNull(cfg.getDictCategoryCode());
            if (columnName != null && ATTR_COLUMN_PATTERN.matcher(columnName).matches()) {
                columnToFieldCode.put(columnName.toLowerCase(), cfg.getFieldCode());
            }
        }
        Map<Long, Map<String, String>> result = new HashMap<>();
        for (Map<String, Object> row : extRows) {
            Object objectId = row.get("object_id");
            if (!(objectId instanceof Number number)) {
                continue;
            }
            Long detailId = number.longValue();
            Map<String, String> values = new HashMap<>();
            for (Map.Entry<String, String> e : columnToFieldCode.entrySet()) {
                Object raw = row.get(e.getKey());
                if (raw == null) {
                    continue;
                }
                String str = raw instanceof BigDecimal ? ((BigDecimal) raw).stripTrailingZeros().toPlainString() : String.valueOf(raw);
                if (StringUtils.hasText(str)) {
                    values.put(e.getValue(), str.trim());
                }
            }
            if (!values.isEmpty()) {
                result.put(detailId, values);
            }
        }
        return result;
    }

    private void persistArchiveExtValues(Long archiveId, List<DocumentTypeExtFieldResponse> fields, Map<String, String> extValues) {
        if (extValues == null || extValues.isEmpty()) {
            return;
        }
        Map<String, DocumentTypeExtFieldResponse> fieldMap = new HashMap<>();
        for (DocumentTypeExtFieldResponse f : fields) {
            fieldMap.put(f.getFieldCode(), f);
        }
        extValues.forEach((fieldCode, value) -> {
            if (!StringUtils.hasText(value) || !fieldMap.containsKey(fieldCode)) {
                return;
            }
            DocumentTypeExtFieldResponse field = fieldMap.get(fieldCode);
            ArchiveExtValue entity = new ArchiveExtValue();
            entity.setArchiveId(archiveId);
            entity.setFieldCode(fieldCode);
            entity.setFieldNameSnapshot(field.getFieldName());
            entity.setFieldType(field.getFieldType());
            entity.setDictCategoryCode(field.getDictCategoryCode());
            if ("DICT".equals(field.getFieldType())) {
                entity.setDictItemCode(value.trim());
                entity.setDictItemNameSnapshot(value.trim());
            } else {
                entity.setTextValue(value.trim());
            }
            entity.setValueSource("MANUAL");
            entity.setCreatedBy(SYSTEM_OPERATOR_ID);
            entity.setCreationDate(LocalDateTime.now());
            entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            entity.setLastUpdateDate(LocalDateTime.now());
            archiveExtValueMapper.insert(entity);
        });
    }

    private ArchiveDefaultResolveResponse resolveDefaultsForDetail(String companyProjectCode,
                                                                   String documentTypeCode,
                                                                   String customRule,
                                                                   String archiveDestination) {
        CompanyProject companyProject = requireCompanyProject(companyProjectCode);
        List<ArchiveFlowRule> rules = archiveFlowRuleMapper.selectList(new LambdaQueryWrapper<ArchiveFlowRule>()
            .eq(ArchiveFlowRule::getCompanyProjectCode, companyProjectCode)
            .eq(ArchiveFlowRule::getDocumentTypeCode, documentTypeCode)
            .eq(ArchiveFlowRule::getDeleteFlag, "N")
            .eq(ArchiveFlowRule::getEnabledFlag, "Y"));
        ArchiveFlowRule bestMatch = rules.stream()
            .max(Comparator.comparingInt(rule -> scoreRule(rule, customRule, archiveDestination)))
            .orElse(null);
        ArchiveDefaultResolveResponse response = new ArchiveDefaultResolveResponse();
        response.setCountryCode(companyProject.getCountryCode());
        if (bestMatch != null) {
            response.setSecurityLevelCode(bestMatch.getSecurityLevelCode());
            response.setArchiveDestination(StringUtils.hasText(archiveDestination) ? archiveDestination : bestMatch.getArchiveDestination());
            response.setDocumentOrganizationCode(bestMatch.getDocumentOrganizationCode());
            response.setRetentionPeriodYears(bestMatch.getRetentionPeriodYears());
        } else {
            response.setArchiveDestination(archiveDestination);
        }
        return response;
    }

    private int scoreRule(ArchiveFlowRule rule, String customRule, String archiveDestination) {
        int score = 0;
        if (Objects.equals(trimToNull(rule.getCustomRule()), trimToNull(customRule))) {
            score += 2;
        }
        if (Objects.equals(trimToNull(rule.getArchiveDestination()), trimToNull(archiveDestination))) {
            score += 2;
        }
        if (!StringUtils.hasText(rule.getCustomRule())) {
            score += 1;
        }
        if (!StringUtils.hasText(rule.getArchiveDestination())) {
            score += 1;
        }
        return score;
    }

    private CompanyProject requireCompanyProject(String companyProjectCode) {
        if (!StringUtils.hasText(companyProjectCode)) {
            throw new BusinessException("公司/项目编码不能为空");
        }
        CompanyProject project = companyProjectMapper.selectOne(new LambdaQueryWrapper<CompanyProject>()
            .eq(CompanyProject::getCompanyProjectCode, companyProjectCode.trim())
            .eq(CompanyProject::getDeleteFlag, "N")
            .eq(CompanyProject::getEnabledFlag, "Y")
            .last("limit 1"));
        if (project == null) {
            throw new BusinessException("公司/项目不存在或已停用: " + companyProjectCode);
        }
        return project;
    }

    private String firstSecurityLevelCode() {
        SecurityLevelDictionary row = securityLevelDictionaryMapper.selectOne(new LambdaQueryWrapper<SecurityLevelDictionary>()
            .eq(SecurityLevelDictionary::getEnabledFlag, "Y")
            .eq(SecurityLevelDictionary::getDeleteFlag, "N")
            .orderByAsc(SecurityLevelDictionary::getSortOrder)
            .last("limit 1"));
        if (row == null || !StringUtils.hasText(row.getSecurityLevelCode())) {
            throw new BusinessException("未配置密级字典，无法从移交生成档案");
        }
        return row.getSecurityLevelCode().trim();
    }

    private String firstDocumentOrganizationCode() {
        List<String> codes = archiveFlowLookupMapper.selectEnabledDocumentOrganizationCodes();
        if (codes == null || codes.isEmpty() || !StringUtils.hasText(codes.get(0))) {
            throw new BusinessException("未配置文档所属机构，无法从移交生成档案");
        }
        return codes.get(0).trim();
    }

    private static String buildRemark(TransferApplication application, TransferApplicationDetail detail) {
        StringBuilder sb = new StringBuilder();
        sb.append("移交申请 ").append(application.getApplicationNumber());
        if (StringUtils.hasText(detail.getCatalogVolumeNo())) {
            sb.append(" 册号:").append(detail.getCatalogVolumeNo().trim());
        }
        if (StringUtils.hasText(detail.getRemark())) {
            sb.append(" ").append(detail.getRemark().trim());
        }
        return sb.toString();
    }

    private static String toYearMonthPeriod(String period, LocalDate fallback) {
        if (StringUtils.hasText(period)) {
            String normalized = period.trim();
            if (normalized.length() >= 7) {
                return normalized.substring(0, 7);
            }
        }
        return YearMonth.from(fallback).toString();
    }

    private static String resolveCarrierType(String carrierType) {
        if (!StringUtils.hasText(carrierType)) {
            return "ELECTRONIC";
        }
        String u = carrierType.trim().toUpperCase(Locale.ROOT);
        if ("ELECTRONIC".equals(u) || "PAPER".equals(u) || "HYBRID".equals(u)) {
            return u;
        }
        return "ELECTRONIC";
    }

    private static String generateCode(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary.trim();
        }
        return fallback;
    }

    private static String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
