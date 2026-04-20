package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.service.TransferBusinessModuleExtFieldService;
import com.smartarchive.businessmodule.domain.BusinessModuleExtField;
import com.smartarchive.businessmodule.mapper.BusinessModuleExtFieldMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TransferBusinessModuleExtFieldServiceImpl implements TransferBusinessModuleExtFieldService {

    private final BusinessModuleExtFieldMapper businessModuleExtFieldMapper;

    @Override
    public List<DocumentTypeExtFieldResponse> listEffectiveForTransfer(String moduleCode) {
        if (!StringUtils.hasText(moduleCode)) {
            return List.of();
        }
        String trimmed = moduleCode.trim();
        List<BusinessModuleExtField> rows = businessModuleExtFieldMapper.selectList(new LambdaQueryWrapper<BusinessModuleExtField>()
            .eq(BusinessModuleExtField::getModuleCode, trimmed)
            .eq(BusinessModuleExtField::getDeleteFlag, "N")
            .eq(BusinessModuleExtField::getEnabledFlag, "Y")
            .eq(BusinessModuleExtField::getFieldScope, "BASIC")
            .orderByAsc(BusinessModuleExtField::getSortOrder)
            .orderByAsc(BusinessModuleExtField::getFieldCode));
        return rows.stream()
            .filter(row -> includesApplicationFunction(row.getApplicationFunctions(), APPLICATION_FUNCTION_TRANSFER))
            .map(this::toDocumentTypeExtFieldResponse)
            .toList();
    }

    @Override
    public Map<String, DocumentTypeExtFieldResponse> asConfigMap(String moduleCode) {
        return listEffectiveForTransfer(moduleCode).stream()
            .collect(Collectors.toMap(DocumentTypeExtFieldResponse::getFieldCode, f -> f, (a, b) -> a));
    }

    @Override
    public Map<String, String> columnToFieldCodeMap(String moduleCode) {
        Map<String, String> map = new HashMap<>();
        for (DocumentTypeExtFieldResponse f : listEffectiveForTransfer(moduleCode)) {
            String col = f.getDictCategoryCode();
            if (StringUtils.hasText(col)) {
                map.put(col.trim().toLowerCase(Locale.ROOT), f.getFieldCode());
            }
        }
        return map;
    }

    private boolean includesApplicationFunction(String applicationFunctionsCsv, String fn) {
        if (!StringUtils.hasText(applicationFunctionsCsv) || !StringUtils.hasText(fn)) {
            return false;
        }
        for (String part : applicationFunctionsCsv.split(",")) {
            if (fn.equals(part.trim())) {
                return true;
            }
        }
        return false;
    }

    private DocumentTypeExtFieldResponse toDocumentTypeExtFieldResponse(BusinessModuleExtField row) {
        String dictColumn = StringUtils.hasText(row.getExtAttribute()) ? row.getExtAttribute().trim().toLowerCase(Locale.ROOT) : null;
        return DocumentTypeExtFieldResponse.builder()
            .fieldId(row.getFieldId())
            .fieldCode(row.getFieldCode())
            .busiModuleCode(row.getModuleCode())
            .fieldName(row.getFieldName())
            .fieldType(mapDataTypeToFieldType(row.getDataType()))
            .dictCategoryCode(dictColumn)
            .requiredFlag(row.getRequiredFlag())
            .enabledFlag(row.getEnabledFlag())
            .formSortOrder(row.getSortOrder() == null ? 1 : row.getSortOrder())
            .lastUpdateDate(row.getLastUpdateDate())
            .build();
    }

    private String mapDataTypeToFieldType(String dataType) {
        if (!StringUtils.hasText(dataType)) {
            return "TEXT";
        }
        String upper = dataType.trim().toUpperCase(Locale.ROOT);
        return switch (upper) {
            case "NUMBER" -> "NUMBER";
            case "DATE", "DATETIME" -> "DATE";
            default -> "TEXT";
        };
    }
}
