package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archivemanage.domain.TransferApplyFieldConfig;
import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigItem;
import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigResponse;
import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigSaveCommand;
import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigSaveItemCommand;
import com.smartarchive.archivemanage.mapper.TransferApplyFieldConfigMapper;
import com.smartarchive.archivemanage.service.TransferApplyFieldConfigService;
import com.smartarchive.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TransferApplyFieldConfigServiceImpl implements TransferApplyFieldConfigService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;

    private static final LinkedHashMap<String, FieldMeta> BUILT_IN_FIELDS = new LinkedHashMap<>();

    static {
        BUILT_IN_FIELDS.put("companyProjectCode", new FieldMeta("公司", 10));
        BUILT_IN_FIELDS.put("docBusiNo", new FieldMeta("文档业务编码", 20));
        BUILT_IN_FIELDS.put("docName", new FieldMeta("文档名称", 30));
        BUILT_IN_FIELDS.put("busiModuleCode", new FieldMeta("业务模块", 40));
        BUILT_IN_FIELDS.put("archPlaceAlpha2Code", new FieldMeta("归档地", 50));
        BUILT_IN_FIELDS.put("documentOrganizationCode", new FieldMeta("文档组织", 60));
        BUILT_IN_FIELDS.put("startArchPeriod", new FieldMeta("开始档期", 70));
        BUILT_IN_FIELDS.put("endArchPeriod", new FieldMeta("结束档期", 80));
        BUILT_IN_FIELDS.put("docGenerationDate", new FieldMeta("文档生成日期", 90));
        BUILT_IN_FIELDS.put("carrierType", new FieldMeta("载体类型", 100));
        BUILT_IN_FIELDS.put("archCopies", new FieldMeta("份数", 110));
        BUILT_IN_FIELDS.put("remark", new FieldMeta("备注", 120));
        BUILT_IN_FIELDS.put("description", new FieldMeta("描述", 130));
    }

    private final TransferApplyFieldConfigMapper transferApplyFieldConfigMapper;

    @Override
    public TransferApplyFieldConfigResponse getByDocumentTypeCode(String documentTypeCode, Long tenantid) {
        String normalizedDocType = requireText(documentTypeCode, "documentTypeCode");
        Long effectiveTenantId = tenantid == null ? 1L : tenantid;
        Map<String, TransferApplyFieldConfig> dbMap = queryDbFieldMap(normalizedDocType, effectiveTenantId);
        List<TransferApplyFieldConfigItem> fields = BUILT_IN_FIELDS.entrySet().stream()
            .map(entry -> {
                TransferApplyFieldConfig dbRow = dbMap.get(entry.getKey());
                String visibleFlag = dbRow == null ? "Y" : normalizeFlag(dbRow.getVisibleFlag(), "Y");
                return TransferApplyFieldConfigItem.builder()
                    .fieldCode(entry.getKey())
                    .fieldName(entry.getValue().fieldName())
                    .visibleFlag(visibleFlag)
                    .sortOrder(entry.getValue().sortOrder())
                    .build();
            })
            .toList();
        return TransferApplyFieldConfigResponse.builder()
            .documentTypeCode(normalizedDocType)
            .fields(fields)
            .build();
    }

    @Override
    @Transactional
    public TransferApplyFieldConfigResponse saveByDocumentTypeCode(String documentTypeCode, TransferApplyFieldConfigSaveCommand command) {
        String normalizedDocType = requireText(documentTypeCode, "documentTypeCode");
        Long tenantid = command.getTenantid();
        if (tenantid == null) {
            throw new BusinessException("tenantid cannot be null");
        }
        Map<String, String> inputVisibleFlags = normalizeInput(command.getFields());
        Map<String, TransferApplyFieldConfig> dbMap = queryDbFieldMap(normalizedDocType, tenantid);
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, FieldMeta> entry : BUILT_IN_FIELDS.entrySet()) {
            String fieldCode = entry.getKey();
            FieldMeta meta = entry.getValue();
            String visibleFlag = inputVisibleFlags.getOrDefault(fieldCode, "Y");
            TransferApplyFieldConfig existing = dbMap.get(fieldCode);
            if (existing == null) {
                TransferApplyFieldConfig created = new TransferApplyFieldConfig();
                created.setDocumentTypeCode(normalizedDocType);
                created.setFieldCode(fieldCode);
                created.setFieldName(meta.fieldName());
                created.setVisibleFlag(visibleFlag);
                created.setSortOrder(meta.sortOrder());
                created.setEnableFlag("Y");
                created.setDeleteFlag("N");
                created.setCreatedBy(SYSTEM_OPERATOR_ID);
                created.setCreationDate(now);
                created.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
                created.setLastUpdateDate(now);
                created.setTenantid(tenantid);
                transferApplyFieldConfigMapper.insert(created);
            } else {
                existing.setFieldName(meta.fieldName());
                existing.setVisibleFlag(visibleFlag);
                existing.setSortOrder(meta.sortOrder());
                existing.setEnableFlag("Y");
                existing.setDeleteFlag("N");
                existing.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
                existing.setLastUpdateDate(now);
                transferApplyFieldConfigMapper.updateById(existing);
            }
        }
        return getByDocumentTypeCode(normalizedDocType, tenantid);
    }

    @Override
    public Map<String, Boolean> visibilityMapByDocumentTypeCode(String documentTypeCode, Long tenantid) {
        TransferApplyFieldConfigResponse response = getByDocumentTypeCode(documentTypeCode, tenantid);
        return response.getFields().stream()
            .collect(Collectors.toMap(
                TransferApplyFieldConfigItem::getFieldCode,
                item -> "Y".equalsIgnoreCase(item.getVisibleFlag()),
                (left, right) -> left,
                LinkedHashMap::new));
    }

    private Map<String, TransferApplyFieldConfig> queryDbFieldMap(String documentTypeCode, Long tenantid) {
        List<TransferApplyFieldConfig> rows = transferApplyFieldConfigMapper.selectList(
            new LambdaQueryWrapper<TransferApplyFieldConfig>()
                .eq(TransferApplyFieldConfig::getDocumentTypeCode, documentTypeCode)
                .eq(TransferApplyFieldConfig::getTenantid, tenantid)
                .eq(TransferApplyFieldConfig::getDeleteFlag, "N")
        );
        return rows.stream()
            .filter(row -> StringUtils.hasText(row.getFieldCode()))
            .collect(Collectors.toMap(
                TransferApplyFieldConfig::getFieldCode,
                Function.identity(),
                (left, right) -> left,
                LinkedHashMap::new));
    }

    private Map<String, String> normalizeInput(List<TransferApplyFieldConfigSaveItemCommand> fields) {
        Map<String, String> map = new LinkedHashMap<>();
        if (fields == null) {
            return map;
        }
        for (TransferApplyFieldConfigSaveItemCommand item : fields) {
            String fieldCode = requireText(item.getFieldCode(), "fieldCode");
            if (!BUILT_IN_FIELDS.containsKey(fieldCode)) {
                throw new BusinessException("unsupported fieldCode: " + fieldCode);
            }
            map.put(fieldCode, normalizeFlag(item.getVisibleFlag(), null));
        }
        return map;
    }

    private String normalizeFlag(String flag, String defaultValue) {
        if (!StringUtils.hasText(flag)) {
            return defaultValue;
        }
        String normalized = flag.trim().toUpperCase();
        if (!"Y".equals(normalized) && !"N".equals(normalized)) {
            throw new BusinessException("visibleFlag only supports Y or N");
        }
        return normalized;
    }

    private String requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private record FieldMeta(String fieldName, Integer sortOrder) {
    }
}
