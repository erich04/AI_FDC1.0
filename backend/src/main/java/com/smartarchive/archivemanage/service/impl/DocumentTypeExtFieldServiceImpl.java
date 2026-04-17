package com.smartarchive.archivemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.archivemanage.domain.ArchiveExtFieldConfig;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldCreateCommand;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldUpdateCommand;
import com.smartarchive.archivemanage.mapper.ArchiveExtFieldConfigMapper;
import com.smartarchive.archivemanage.service.DocumentTypeExtFieldService;
import com.smartarchive.businessmodule.domain.BusinessModule;
import com.smartarchive.businessmodule.mapper.BusinessModuleMapper;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.dictionary.domain.DictionaryItem;
import com.smartarchive.dictionary.mapper.DictionaryItemMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DocumentTypeExtFieldServiceImpl implements DocumentTypeExtFieldService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final String FUNCTION_MODULE_CATEGORY_CODE = "FUNCTION_MODULE";

    private final ArchiveExtFieldConfigMapper archiveExtFieldConfigMapper;
    private final BusinessModuleMapper businessModuleMapper;
    private final DictionaryItemMapper dictionaryItemMapper;

    @Override
    public List<DocumentTypeExtFieldResponse> listDirect(String busiModuleCode) {
        BusinessModule module = requireBusinessModule(busiModuleCode);
        return archiveExtFieldConfigMapper.selectList(new LambdaQueryWrapper<ArchiveExtFieldConfig>()
                .eq(ArchiveExtFieldConfig::getBusiModuleCode, module.getModuleCode())
                .eq(ArchiveExtFieldConfig::getDeleteFlag, "N")
                .orderByAsc(ArchiveExtFieldConfig::getFormSortOrder)
                .orderByAsc(ArchiveExtFieldConfig::getFieldCode))
            .stream()
            .map(item -> toResponse(item, module.getLevelNum(), module.getModuleCode()))
            .toList();
    }

    @Override
    public List<DocumentTypeExtFieldResponse> listEffective(String busiModuleCode) {
        BusinessModule current = requireBusinessModule(busiModuleCode);
        List<String> typeCodes = new ArrayList<>();
        if (StringUtils.hasText(current.getAncestorPath())) {
            typeCodes.addAll(List.of(current.getAncestorPath().split("/")));
        }
        typeCodes.add(current.getModuleCode());
        Map<String, BusinessModule> typeMap = businessModuleMapper.selectList(new LambdaQueryWrapper<BusinessModule>()
                .in(BusinessModule::getModuleCode, typeCodes)
                .eq(BusinessModule::getDeleteFlag, "N"))
            .stream()
            .collect(Collectors.toMap(BusinessModule::getModuleCode, Function.identity(), (left, right) -> left));

        return archiveExtFieldConfigMapper.selectList(new LambdaQueryWrapper<ArchiveExtFieldConfig>()
                .in(ArchiveExtFieldConfig::getBusiModuleCode, typeCodes)
                .eq(ArchiveExtFieldConfig::getDeleteFlag, "N")
                .eq(ArchiveExtFieldConfig::getEnabledFlag, "Y")
                .orderByAsc(ArchiveExtFieldConfig::getFormSortOrder)
                .orderByAsc(ArchiveExtFieldConfig::getFieldCode))
            .stream()
            .sorted(Comparator.comparing((ArchiveExtFieldConfig item) -> typeMap.get(item.getBusiModuleCode()).getLevelNum())
                .thenComparing(ArchiveExtFieldConfig::getFormSortOrder)
                .thenComparing(ArchiveExtFieldConfig::getFieldCode))
            .map(item -> {
                BusinessModule source = typeMap.get(item.getBusiModuleCode());
                return toResponse(item, source == null ? null : source.getLevelNum(), item.getBusiModuleCode());
            })
            .toList();
    }

    @Override
    @Transactional
    public DocumentTypeExtFieldResponse create(String busiModuleCode, DocumentTypeExtFieldCreateCommand command) {
        BusinessModule module = requireBusinessModule(busiModuleCode);
        validateCommand(command.getFieldType(), command.getRequiredFlag(), command.getEnabledFlag(), command.getQueryEnabledFlag(),
            command.getDictCategoryCode(), command.getUsageModule(), command.getRelatedModuleCode(), command.getRelatedField());

        ArchiveExtFieldConfig entity = new ArchiveExtFieldConfig();
        entity.setFieldCode(generateFieldCode(module.getModuleCode()));
        entity.setBusiModuleCode(module.getModuleCode());
        entity.setUsageModule(trimRequiredText(command.getUsageModule(), "usageModule"));
        entity.setRelatedModuleCode(trimRequiredText(command.getRelatedModuleCode(), "relatedModuleCode"));
        entity.setRelatedField(trimRequiredText(command.getRelatedField(), "relatedField"));
        entity.setFieldName(command.getFieldName().trim());
        entity.setFieldType(command.getFieldType().trim().toUpperCase());
        entity.setDictCategoryCode(trimToNull(command.getDictCategoryCode()));
        entity.setSemanticCode(trimToNull(command.getSemanticCode()));
        entity.setRequiredFlag(normalizeFlag(command.getRequiredFlag(), "N"));
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        entity.setFormSortOrder(command.getFormSortOrder() == null ? 1 : command.getFormSortOrder());
        entity.setQueryEnabledFlag(normalizeFlag(command.getQueryEnabledFlag(), "N"));
        entity.setQuerySortOrder(command.getQuerySortOrder() == null ? 1 : command.getQuerySortOrder());
        entity.setDeleteFlag("N");
        entity.setCreatedBy(SYSTEM_OPERATOR_ID);
        entity.setCreationDate(LocalDateTime.now());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        archiveExtFieldConfigMapper.insert(entity);
        return toResponse(entity, module.getLevelNum(), module.getModuleCode());
    }

    @Override
    @Transactional
    public DocumentTypeExtFieldResponse update(String busiModuleCode, String fieldCode, DocumentTypeExtFieldUpdateCommand command) {
        BusinessModule module = requireBusinessModule(busiModuleCode);
        validateCommand(command.getFieldType(), command.getRequiredFlag(), command.getEnabledFlag(), command.getQueryEnabledFlag(),
            command.getDictCategoryCode(), command.getUsageModule(), command.getRelatedModuleCode(), command.getRelatedField());
        ArchiveExtFieldConfig entity = requireField(busiModuleCode, fieldCode);
        entity.setUsageModule(trimRequiredText(command.getUsageModule(), "usageModule"));
        entity.setRelatedModuleCode(trimRequiredText(command.getRelatedModuleCode(), "relatedModuleCode"));
        entity.setRelatedField(trimRequiredText(command.getRelatedField(), "relatedField"));
        entity.setFieldName(command.getFieldName().trim());
        entity.setFieldType(command.getFieldType().trim().toUpperCase());
        entity.setDictCategoryCode(trimToNull(command.getDictCategoryCode()));
        entity.setSemanticCode(trimToNull(command.getSemanticCode()));
        entity.setRequiredFlag(normalizeFlag(command.getRequiredFlag(), "N"));
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        entity.setFormSortOrder(command.getFormSortOrder() == null ? 1 : command.getFormSortOrder());
        entity.setQueryEnabledFlag(normalizeFlag(command.getQueryEnabledFlag(), "N"));
        entity.setQuerySortOrder(command.getQuerySortOrder() == null ? 1 : command.getQuerySortOrder());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        archiveExtFieldConfigMapper.updateById(entity);
        return toResponse(entity, module.getLevelNum(), module.getModuleCode());
    }

    @Override
    @Transactional
    public void delete(String busiModuleCode, String fieldCode) {
        requireBusinessModule(busiModuleCode);
        ArchiveExtFieldConfig entity = requireField(busiModuleCode, fieldCode);
        archiveExtFieldConfigMapper.update(null, new LambdaUpdateWrapper<ArchiveExtFieldConfig>()
            .eq(ArchiveExtFieldConfig::getFieldId, entity.getFieldId())
            .set(ArchiveExtFieldConfig::getDeleteFlag, "Y")
            .set(ArchiveExtFieldConfig::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
            .set(ArchiveExtFieldConfig::getLastUpdateDate, LocalDateTime.now()));
    }

    private BusinessModule requireBusinessModule(String busiModuleCode) {
        BusinessModule module = businessModuleMapper.selectOne(new LambdaQueryWrapper<BusinessModule>()
            .eq(BusinessModule::getModuleCode, busiModuleCode)
            .eq(BusinessModule::getDeleteFlag, "N")
            .last("limit 1"));
        if (module == null) {
            throw new BusinessException("Business module does not exist");
        }
        return module;
    }

    private ArchiveExtFieldConfig requireField(String busiModuleCode, String fieldCode) {
        ArchiveExtFieldConfig field = archiveExtFieldConfigMapper.selectOne(new LambdaQueryWrapper<ArchiveExtFieldConfig>()
            .eq(ArchiveExtFieldConfig::getBusiModuleCode, busiModuleCode)
            .eq(ArchiveExtFieldConfig::getFieldCode, fieldCode)
            .eq(ArchiveExtFieldConfig::getDeleteFlag, "N")
            .last("limit 1"));
        if (field == null) {
            throw new BusinessException("Extension field does not exist");
        }
        return field;
    }

    private void validateCommand(String fieldType, String requiredFlag, String enabledFlag, String queryEnabledFlag, String dictCategoryCode,
                                 String usageModule, String relatedModuleCode, String relatedField) {
        String normalizedFieldType = fieldType == null ? "" : fieldType.trim().toUpperCase();
        if (!List.of("TEXT", "DICT").contains(normalizedFieldType)) {
            throw new BusinessException("fieldType only supports TEXT or DICT");
        }
        if ("DICT".equals(normalizedFieldType) && !StringUtils.hasText(dictCategoryCode)) {
            throw new BusinessException("dictCategoryCode is required when fieldType is DICT");
        }
        validateFlag(requiredFlag, "requiredFlag");
        validateFlag(enabledFlag, "enabledFlag");
        validateFlag(queryEnabledFlag, "queryEnabledFlag");
        validateFunctionModuleItem(usageModule, "usageModule");
        validateFunctionModuleItem(relatedModuleCode, "relatedModuleCode");
        trimRequiredText(relatedField, "relatedField");
    }

    private void validateFlag(String flag, String fieldName) {
        String normalized = normalizeFlag(flag, null);
        if (!List.of("Y", "N").contains(normalized)) {
            throw new BusinessException(fieldName + " only supports Y or N");
        }
    }

    private String normalizeFlag(String flag, String defaultValue) {
        if (!StringUtils.hasText(flag)) {
            return defaultValue;
        }
        return flag.trim().toUpperCase();
    }

    private String generateFieldCode(String busiModuleCode) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String prefix = busiModuleCode.replaceAll("[^A-Za-z0-9]", "");
        prefix = prefix.length() > 12 ? prefix.substring(0, 12) : prefix;
        return "EXT_" + prefix + "_" + suffix;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String trimRequiredText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private void validateFunctionModuleItem(String itemCode, String fieldName) {
        String normalizedItemCode = trimRequiredText(itemCode, fieldName);
        long count = dictionaryItemMapper.selectCount(new LambdaQueryWrapper<DictionaryItem>()
            .eq(DictionaryItem::getCategoryCode, FUNCTION_MODULE_CATEGORY_CODE)
            .eq(DictionaryItem::getItemCode, normalizedItemCode)
            .eq(DictionaryItem::getEnabledFlag, "Y")
            .eq(DictionaryItem::getDeleteFlag, "N"));
        if (count == 0) {
            throw new BusinessException(fieldName + " must be an enabled dictionary item of FUNCTION_MODULE");
        }
    }

    private DocumentTypeExtFieldResponse toResponse(ArchiveExtFieldConfig item, Integer sourceLevel, String sourceBusiModuleCode) {
        return DocumentTypeExtFieldResponse.builder()
            .fieldId(item.getFieldId())
            .fieldCode(item.getFieldCode())
            .busiModuleCode(item.getBusiModuleCode())
            .usageModule(item.getUsageModule())
            .relatedModuleCode(item.getRelatedModuleCode())
            .relatedField(item.getRelatedField())
            .fieldName(item.getFieldName())
            .fieldType(item.getFieldType())
            .dictCategoryCode(item.getDictCategoryCode())
            .semanticCode(item.getSemanticCode())
            .requiredFlag(item.getRequiredFlag())
            .enabledFlag(item.getEnabledFlag())
            .formSortOrder(item.getFormSortOrder())
            .queryEnabledFlag(item.getQueryEnabledFlag())
            .querySortOrder(item.getQuerySortOrder())
            .sourceLevel(sourceLevel)
            .sourceBusiModuleCode(sourceBusiModuleCode)
            .lastUpdateDate(item.getLastUpdateDate())
            .build();
    }
}
