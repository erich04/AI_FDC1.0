package com.smartarchive.businessmodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.businessmodule.domain.BusinessModule;
import com.smartarchive.businessmodule.domain.BusinessModuleExtField;
import com.smartarchive.businessmodule.dto.BusinessModuleCommand;
import com.smartarchive.businessmodule.dto.BusinessModuleExtFieldCommand;
import com.smartarchive.businessmodule.dto.BusinessModuleExtFieldResponse;
import com.smartarchive.businessmodule.dto.BusinessModuleNodeResponse;
import com.smartarchive.businessmodule.dto.BusinessModuleParentOptionResponse;
import com.smartarchive.businessmodule.dto.BusinessModuleUpdateCommand;
import com.smartarchive.businessmodule.mapper.BusinessModuleExtFieldMapper;
import com.smartarchive.businessmodule.mapper.BusinessModuleMapper;
import com.smartarchive.businessmodule.service.BusinessModuleService;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.documenttypeconfig.domain.DocumentTypeConfig;
import com.smartarchive.documenttypeconfig.mapper.DocumentTypeConfigMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BusinessModuleServiceImpl implements BusinessModuleService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;
    private static final int MAX_LEVEL = 6;
    private static final List<String> SUPPORTED_APPLICATION_FUNCTIONS = List.of("应收", "移交");
    private static final List<String> SUPPORTED_EXT_ATTRIBUTES = List.of("ATTR1", "ATTR2", "ATTR3", "ATTR4", "ATTR5", "ATTR6");

    private final BusinessModuleMapper businessModuleMapper;
    private final BusinessModuleExtFieldMapper extFieldMapper;
    private final DocumentTypeConfigMapper documentTypeConfigMapper;

    @Override
    public List<BusinessModuleNodeResponse> listTree() {
        syncDocumentTypesToBusinessModules();
        List<BusinessModule> modules = businessModuleMapper.selectList(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getDeleteFlag, "N")
                .orderByAsc(BusinessModule::getLevelNum)
                .orderByAsc(BusinessModule::getSortOrder)
                .orderByAsc(BusinessModule::getModuleCode));
        Map<String, BusinessModuleNodeResponse> map = modules.stream()
                .map(this::toNode)
                .collect(Collectors.toMap(BusinessModuleNodeResponse::getModuleCode, Function.identity(), (a, b) -> a));
        List<BusinessModuleNodeResponse> roots = new ArrayList<>();
        for (BusinessModule module : modules) {
            BusinessModuleNodeResponse node = map.get(module.getModuleCode());
            if (!StringUtils.hasText(module.getParentCode()) || !map.containsKey(module.getParentCode())) {
                roots.add(node);
            } else {
                map.get(module.getParentCode()).getChildren().add(node);
            }
        }
        sortTree(roots);
        return roots;
    }

    @Override
    public List<BusinessModuleParentOptionResponse> listParentOptions() {
        syncDocumentTypesToBusinessModules();
        Map<String, BusinessModuleParentOptionResponse> optionMap = new LinkedHashMap<>();
        List<BusinessModule> businessModules = businessModuleMapper.selectList(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getDeleteFlag, "N")
                .orderByAsc(BusinessModule::getLevelNum)
                .orderByAsc(BusinessModule::getSortOrder)
                .orderByAsc(BusinessModule::getModuleCode));
        businessModules.forEach(module -> optionMap.putIfAbsent(module.getModuleCode(), toParentOption(module)));

        List<DocumentTypeConfig> documentTypes = documentTypeConfigMapper.selectList(new LambdaQueryWrapper<DocumentTypeConfig>()
                .eq(DocumentTypeConfig::getDeleteFlag, "N")
                .orderByAsc(DocumentTypeConfig::getDocTypeCode));
        documentTypes.forEach(type -> optionMap.putIfAbsent(type.getDocTypeCode(), toParentOption(type)));

        return new ArrayList<>(optionMap.values());
    }

    @Override
    @Transactional
    public BusinessModuleNodeResponse create(BusinessModuleCommand command) {
        ensureCodeAvailable(command.getModuleCode().trim());
        TreeMeta meta = resolveMeta(command.getParentCode(), null);
        BusinessModule entity = new BusinessModule();
        entity.setModuleCode(command.getModuleCode().trim());
        entity.setModuleName(command.getModuleName().trim());
        entity.setParentCode(trimToNull(command.getParentCode()));
        entity.setLevelNum(meta.levelNum());
        entity.setAncestorPath(meta.ancestorPath());
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        entity.setSortOrder(command.getSortOrder() == null ? nextSortOrder(command.getParentCode()) : command.getSortOrder());
        entity.setSecurityLevel(normalizeSecurityLevel(command.getSecurityLevel()));
        entity.setIntegrationType(normalizeIntegrationType(command.getIntegrationType()));
        entity.setDescription(trimToNull(command.getDescription()));
        entity.setRemark(trimToNull(command.getRemark()));
        entity.setDeleteFlag("N");
        entity.setCreatedBy(SYSTEM_OPERATOR_ID);
        entity.setCreationDate(LocalDateTime.now());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        businessModuleMapper.insert(entity);
        return toNode(entity);
    }

    @Override
    @Transactional
    public BusinessModuleNodeResponse update(String moduleCode, BusinessModuleUpdateCommand command) {
        BusinessModule entity = requireModule(moduleCode);
        TreeMeta meta = resolveMeta(command.getParentCode(), moduleCode);
        entity.setModuleName(command.getModuleName().trim());
        entity.setParentCode(trimToNull(command.getParentCode()));
        entity.setLevelNum(meta.levelNum());
        entity.setAncestorPath(meta.ancestorPath());
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        entity.setSortOrder(command.getSortOrder() == null ? entity.getSortOrder() : command.getSortOrder());
        entity.setSecurityLevel(normalizeSecurityLevel(command.getSecurityLevel()));
        entity.setIntegrationType(normalizeIntegrationType(command.getIntegrationType()));
        entity.setDescription(trimToNull(command.getDescription()));
        entity.setRemark(trimToNull(command.getRemark()));
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        businessModuleMapper.updateById(entity);
        refreshDescendants(entity);
        return toNode(requireModule(moduleCode));
    }

    @Override
    @Transactional
    public void delete(String moduleCode) {
        BusinessModule entity = requireModule(moduleCode);
        if (hasChildren(moduleCode)) {
            throw new BusinessException("当前业务模块存在下级节点，不能直接删除");
        }
        businessModuleMapper.update(null, new LambdaUpdateWrapper<BusinessModule>()
                .eq(BusinessModule::getId, entity.getId())
                .set(BusinessModule::getDeleteFlag, "Y")
                .set(BusinessModule::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
                .set(BusinessModule::getLastUpdateDate, LocalDateTime.now()));
    }

    @Override
    public List<BusinessModuleExtFieldResponse> listFields(String moduleCode, String fieldScope) {
        requireModule(moduleCode);
        LambdaQueryWrapper<BusinessModuleExtField> wrapper = new LambdaQueryWrapper<BusinessModuleExtField>()
                .eq(BusinessModuleExtField::getModuleCode, moduleCode)
                .eq(BusinessModuleExtField::getDeleteFlag, "N")
                .orderByAsc(BusinessModuleExtField::getSortOrder)
                .orderByAsc(BusinessModuleExtField::getFieldCode);
        if (StringUtils.hasText(fieldScope)) {
            wrapper.eq(BusinessModuleExtField::getFieldScope, fieldScope.trim().toUpperCase());
        }
        return extFieldMapper.selectList(wrapper).stream().map(this::toFieldResponse).toList();
    }

    @Override
    public List<BusinessModuleExtFieldResponse> listFieldsByApplicationFunction(String moduleCode,
                                                                                 String applicationFunction,
                                                                                 String fieldScope) {
        requireModule(moduleCode);
        if (!StringUtils.hasText(applicationFunction)) {
            throw new BusinessException("应用功能不能为空");
        }
        String fn = applicationFunction.trim();
        LambdaQueryWrapper<BusinessModuleExtField> wrapper = new LambdaQueryWrapper<BusinessModuleExtField>()
                .eq(BusinessModuleExtField::getModuleCode, moduleCode)
                .eq(BusinessModuleExtField::getDeleteFlag, "N")
                .orderByAsc(BusinessModuleExtField::getSortOrder)
                .orderByAsc(BusinessModuleExtField::getFieldCode);
        if (StringUtils.hasText(fieldScope)) {
            wrapper.eq(BusinessModuleExtField::getFieldScope, fieldScope.trim().toUpperCase());
        }
        return extFieldMapper.selectList(wrapper).stream()
                .filter(entity -> parseApplicationFunctions(entity.getApplicationFunctions()).contains(fn))
                .map(this::toFieldResponse)
                .toList();
    }

    @Override
    @Transactional
    public BusinessModuleExtFieldResponse createField(String moduleCode, BusinessModuleExtFieldCommand command) {
        requireModule(moduleCode);
        validateField(command);
        List<String> functions = normalizeApplicationFunctionList(command.getApplicationFunctions());
        BusinessModuleExtField first = null;
        for (int i = 0; i < functions.size(); i++) {
            String function = functions.get(i);
            String fieldCode = (i == 0)
                    ? command.getFieldCode().trim()
                    : buildDerivedFieldCode(command.getFieldCode().trim(), function);
            ensureFieldCodeAvailable(fieldCode);

            BusinessModuleExtField entity = new BusinessModuleExtField();
            entity.setFieldCode(fieldCode);
            entity.setModuleCode(moduleCode);
            applyField(entity, command, List.of(function));
            entity.setDeleteFlag("N");
            entity.setCreatedBy(SYSTEM_OPERATOR_ID);
            entity.setCreationDate(LocalDateTime.now());
            entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            entity.setLastUpdateDate(LocalDateTime.now());
            extFieldMapper.insert(entity);
            if (first == null) {
                first = entity;
            }
        }
        return toFieldResponse(first);
    }

    @Override
    @Transactional
    public BusinessModuleExtFieldResponse updateField(String moduleCode, String fieldCode, BusinessModuleExtFieldCommand command) {
        requireModule(moduleCode);
        validateField(command);
        BusinessModuleExtField entity = requireField(moduleCode, fieldCode);
        if (!fieldCode.equals(command.getFieldCode().trim())) {
            throw new BusinessException("字段编码不允许修改");
        }
        applyField(entity, command, command.getApplicationFunctions());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        extFieldMapper.updateById(entity);
        return toFieldResponse(entity);
    }

    @Override
    @Transactional
    public void deleteField(String moduleCode, String fieldCode) {
        BusinessModuleExtField entity = requireField(moduleCode, fieldCode);
        extFieldMapper.update(null, new LambdaUpdateWrapper<BusinessModuleExtField>()
                .eq(BusinessModuleExtField::getFieldId, entity.getFieldId())
                .set(BusinessModuleExtField::getDeleteFlag, "Y")
                .set(BusinessModuleExtField::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
                .set(BusinessModuleExtField::getLastUpdateDate, LocalDateTime.now()));
    }

    private void applyField(BusinessModuleExtField entity, BusinessModuleExtFieldCommand command, List<String> applicationFunctions) {
        entity.setFieldScope(command.getFieldScope().trim().toUpperCase());
        entity.setApplicationFunctions(normalizeApplicationFunctions(applicationFunctions));
        entity.setExtAttribute(normalizeExtAttribute(command.getExtAttribute()));
        entity.setFieldName(command.getFieldName().trim());
        entity.setEnglishFieldName(trimToNull(command.getEnglishFieldName()));
        entity.setDataType(command.getDataType().trim().toUpperCase());
        entity.setQueryFlag(normalizeFlag(command.getQueryFlag(), "N"));
        entity.setRequiredFlag(normalizeFlag(command.getRequiredFlag(), "N"));
        entity.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        entity.setSortOrder(command.getSortOrder() == null ? 1 : command.getSortOrder());
    }

    private void validateField(BusinessModuleExtFieldCommand command) {
        if (!StringUtils.hasText(command.getFieldCode())) {
            throw new BusinessException("字段编码不能为空");
        }
        if (!List.of("BASIC", "ATTACHMENT").contains(command.getFieldScope().trim().toUpperCase())) {
            throw new BusinessException("字段归属仅支持 BASIC 或 ATTACHMENT");
        }
        if (!List.of("TEXT", "NUMBER", "DATE", "DATETIME", "DICT", "BOOLEAN").contains(command.getDataType().trim().toUpperCase())) {
            throw new BusinessException("数据类型仅支持 TEXT、NUMBER、DATE、DATETIME、DICT、BOOLEAN");
        }
    }

    private TreeMeta resolveMeta(String parentCode, String currentCode) {
        if (!StringUtils.hasText(parentCode)) {
            return new TreeMeta(1, "");
        }
        if (parentCode.equals(currentCode)) {
            throw new BusinessException("上级业务模块不能选择自身");
        }
        BusinessModule parent = findModule(parentCode);
        if (parent == null) {
            if (isBusinessModuleCode(parentCode) || isDocumentTypeCode(parentCode)) {
                return new TreeMeta(1, parentCode);
            }
            throw new BusinessException("上级业务模块不存在");
        }
        if (parent.getLevelNum() >= MAX_LEVEL) {
            throw new BusinessException("业务模块最多支持 " + MAX_LEVEL + " 层");
        }
        if (StringUtils.hasText(currentCode) && StringUtils.hasText(parent.getAncestorPath())
                && List.of(parent.getAncestorPath().split("/")).contains(currentCode)) {
            throw new BusinessException("上级业务模块不能选择自身下级");
        }
        String ancestorPath = StringUtils.hasText(parent.getAncestorPath()) ? parent.getAncestorPath() + "/" + parent.getModuleCode() : parent.getModuleCode();
        return new TreeMeta(parent.getLevelNum() + 1, ancestorPath);
    }

    private void refreshDescendants(BusinessModule parent) {
        List<BusinessModule> children = businessModuleMapper.selectList(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getParentCode, parent.getModuleCode())
                .eq(BusinessModule::getDeleteFlag, "N"));
        for (BusinessModule child : children) {
            String ancestorPath = StringUtils.hasText(parent.getAncestorPath()) ? parent.getAncestorPath() + "/" + parent.getModuleCode() : parent.getModuleCode();
            child.setLevelNum(parent.getLevelNum() + 1);
            child.setAncestorPath(ancestorPath);
            child.setLastUpdateDate(LocalDateTime.now());
            businessModuleMapper.updateById(child);
            refreshDescendants(child);
        }
    }

    private BusinessModule requireModule(String moduleCode) {
        BusinessModule module = findModule(moduleCode);
        if (module == null) {
            throw new BusinessException("业务模块不存在");
        }
        return module;
    }

    private BusinessModule findModule(String moduleCode) {
        return businessModuleMapper.selectOne(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getModuleCode, moduleCode)
                .eq(BusinessModule::getDeleteFlag, "N")
                .last("limit 1"));
    }

    private boolean isBusinessModuleCode(String parentCode) {
        return businessModuleMapper.selectCount(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getModuleCode, parentCode)
                .eq(BusinessModule::getDeleteFlag, "N")) > 0;
    }

    private boolean isDocumentTypeCode(String parentCode) {
        return documentTypeConfigMapper.selectCount(new LambdaQueryWrapper<DocumentTypeConfig>()
                .eq(DocumentTypeConfig::getDocTypeCode, parentCode)
                .eq(DocumentTypeConfig::getDeleteFlag, "N")) > 0;
    }

    @Transactional
    protected void syncDocumentTypesToBusinessModules() {
        List<DocumentTypeConfig> documentTypes = documentTypeConfigMapper.selectList(new LambdaQueryWrapper<DocumentTypeConfig>()
                .eq(DocumentTypeConfig::getDeleteFlag, "N")
                .orderByAsc(DocumentTypeConfig::getDocTypeCode));
        if (documentTypes.isEmpty()) {
            return;
        }
        List<BusinessModule> existingModules = businessModuleMapper.selectList(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getDeleteFlag, "N"));
        Map<String, BusinessModule> existingModuleMap = existingModules.stream()
                .filter(module -> StringUtils.hasText(module.getModuleCode()))
                .collect(Collectors.toMap(BusinessModule::getModuleCode, Function.identity(), (a, b) -> a));
        Set<String> existingCodes = new HashSet<>(existingModuleMap.keySet());

        int nextSortOrder = nextSortOrder(null);
        LocalDateTime now = LocalDateTime.now();
        for (DocumentTypeConfig documentType : documentTypes) {
            String code = trimToNull(documentType.getDocTypeCode());
            if (!StringUtils.hasText(code)) {
                continue;
            }
            BusinessModule existing = existingModuleMap.get(code);
            if (existing != null) {
                boolean needFixRoot = code.equals(trimToNull(existing.getParentCode()))
                        || (StringUtils.hasText(existing.getParentCode()) && !existingCodes.contains(existing.getParentCode()))
                        || !Integer.valueOf(1).equals(existing.getLevelNum());
                if (needFixRoot) {
                    businessModuleMapper.update(null, new LambdaUpdateWrapper<BusinessModule>()
                            .eq(BusinessModule::getId, existing.getId())
                            .set(BusinessModule::getParentCode, null)
                            .set(BusinessModule::getLevelNum, 1)
                            .set(BusinessModule::getAncestorPath, "")
                            .set(BusinessModule::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
                            .set(BusinessModule::getLastUpdateDate, now));
                }
                continue;
            }
            BusinessModule entity = new BusinessModule();
            entity.setModuleCode(code);
            entity.setModuleName(StringUtils.hasText(documentType.getDocTypeDescription()) ? documentType.getDocTypeDescription().trim() : code);
            entity.setParentCode(null);
            entity.setLevelNum(1);
            entity.setAncestorPath("");
            entity.setEnabledFlag(normalizeFlag(documentType.getEnableFlag(), "Y"));
            entity.setSecurityLevel("公开");
            entity.setIntegrationType("不集成");
            entity.setDescription(trimToNull(documentType.getDocTypeDescription()));
            entity.setRemark(null);
            entity.setSortOrder(nextSortOrder++);
            entity.setDeleteFlag("N");
            entity.setCreatedBy(SYSTEM_OPERATOR_ID);
            entity.setCreationDate(now);
            entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
            entity.setLastUpdateDate(now);
            businessModuleMapper.insert(entity);
            existingCodes.add(code);
        }
    }

    private BusinessModuleExtField requireField(String moduleCode, String fieldCode) {
        BusinessModuleExtField field = extFieldMapper.selectOne(new LambdaQueryWrapper<BusinessModuleExtField>()
                .eq(BusinessModuleExtField::getModuleCode, moduleCode)
                .eq(BusinessModuleExtField::getFieldCode, fieldCode)
                .eq(BusinessModuleExtField::getDeleteFlag, "N")
                .last("limit 1"));
        if (field == null) {
            throw new BusinessException("扩展字段不存在");
        }
        return field;
    }

    private void ensureCodeAvailable(String moduleCode) {
        Long count = businessModuleMapper.selectCount(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getModuleCode, moduleCode)
                .eq(BusinessModule::getDeleteFlag, "N"));
        if (count > 0) {
            throw new BusinessException("业务模块编码已存在");
        }
    }

    private void ensureFieldCodeAvailable(String fieldCode) {
        Long count = extFieldMapper.selectCount(new LambdaQueryWrapper<BusinessModuleExtField>()
                .eq(BusinessModuleExtField::getFieldCode, fieldCode)
                .eq(BusinessModuleExtField::getDeleteFlag, "N"));
        if (count > 0) {
            throw new BusinessException("字段编码已存在");
        }
    }

    private boolean hasChildren(String moduleCode) {
        return businessModuleMapper.selectCount(new LambdaQueryWrapper<BusinessModule>()
                .eq(BusinessModule::getParentCode, moduleCode)
                .eq(BusinessModule::getDeleteFlag, "N")) > 0;
    }

    private Integer nextSortOrder(String parentCode) {
        Long count = businessModuleMapper.selectCount(new LambdaQueryWrapper<BusinessModule>()
                .eq(StringUtils.hasText(parentCode), BusinessModule::getParentCode, parentCode)
                .isNull(!StringUtils.hasText(parentCode), BusinessModule::getParentCode)
                .eq(BusinessModule::getDeleteFlag, "N"));
        return count.intValue() + 1;
    }

    private String normalizeFlag(String flag, String defaultValue) {
        String normalized = StringUtils.hasText(flag) ? flag.trim().toUpperCase() : defaultValue;
        if (!List.of("Y", "N").contains(normalized)) {
            throw new BusinessException("启用/查询/必填标志仅支持 Y 或 N");
        }
        return normalized;
    }

    private String normalizeSecurityLevel(String securityLevel) {
        String normalized = StringUtils.hasText(securityLevel) ? securityLevel.trim() : "公开";
        if (!List.of("公开", "秘密", "机密").contains(normalized)) {
            throw new BusinessException("密级仅支持：公开、秘密、机密");
        }
        return normalized;
    }

    private String normalizeIntegrationType(String integrationType) {
        String normalized = StringUtils.hasText(integrationType) ? integrationType.trim() : "不集成";
        if (!List.of("全部集成", "部分集成", "不集成").contains(normalized)) {
            throw new BusinessException("集成类型仅支持：全部集成、部分集成、不集成");
        }
        return normalized;
    }

    private String normalizeApplicationFunctions(List<String> applicationFunctions) {
        List<String> normalized = normalizeApplicationFunctionList(applicationFunctions);
        if (normalized.isEmpty() || !SUPPORTED_APPLICATION_FUNCTIONS.containsAll(normalized)) {
            throw new BusinessException("应用功能仅支持：应收、移交");
        }
        return String.join(",", normalized);
    }

    private List<String> normalizeApplicationFunctionList(List<String> applicationFunctions) {
        if (applicationFunctions == null || applicationFunctions.isEmpty()) {
            throw new BusinessException("应用功能不能为空");
        }
        return applicationFunctions.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
    }

    private String buildDerivedFieldCode(String baseFieldCode, String applicationFunction) {
        String suffix = switch (applicationFunction) {
            case "应收" -> "AR";
            case "移交" -> "TR";
            default -> "EXT";
        };
        int maxBaseLength = Math.max(1, 64 - suffix.length() - 1);
        String normalizedBase = baseFieldCode.length() > maxBaseLength ? baseFieldCode.substring(0, maxBaseLength) : baseFieldCode;
        String candidate = normalizedBase + "_" + suffix;
        int serial = 1;
        while (extFieldMapper.selectCount(new LambdaQueryWrapper<BusinessModuleExtField>()
                .eq(BusinessModuleExtField::getFieldCode, candidate)
                .eq(BusinessModuleExtField::getDeleteFlag, "N")) > 0) {
            String serialSuffix = "_" + suffix + serial++;
            int dynamicBaseLength = Math.max(1, 64 - serialSuffix.length());
            String dynamicBase = baseFieldCode.length() > dynamicBaseLength ? baseFieldCode.substring(0, dynamicBaseLength) : baseFieldCode;
            candidate = dynamicBase + serialSuffix;
        }
        return candidate;
    }

    private List<String> parseApplicationFunctions(String applicationFunctions) {
        if (!StringUtils.hasText(applicationFunctions)) {
            return List.of();
        }
        return List.of(applicationFunctions.split(",")).stream().filter(StringUtils::hasText).map(String::trim).toList();
    }

    private String normalizeExtAttribute(String extAttribute) {
        if (!StringUtils.hasText(extAttribute)) {
            throw new BusinessException("扩展字段不能为空");
        }
        String normalized = extAttribute.trim().toUpperCase();
        if (!SUPPORTED_EXT_ATTRIBUTES.contains(normalized)) {
            throw new BusinessException("扩展字段仅支持：ATTR1、ATTR2、ATTR3、ATTR4、ATTR5、ATTR6");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void sortTree(List<BusinessModuleNodeResponse> nodes) {
        nodes.sort(Comparator.comparing(BusinessModuleNodeResponse::getSortOrder).thenComparing(BusinessModuleNodeResponse::getModuleCode));
        nodes.forEach(node -> sortTree(node.getChildren()));
    }

    private BusinessModuleNodeResponse toNode(BusinessModule entity) {
        BusinessModuleNodeResponse node = new BusinessModuleNodeResponse();
        node.setId(entity.getId());
        node.setModuleCode(entity.getModuleCode());
        node.setModuleName(entity.getModuleName());
        node.setParentCode(entity.getParentCode());
        node.setLevelNum(entity.getLevelNum());
        node.setAncestorPath(entity.getAncestorPath());
        node.setEnabledFlag(entity.getEnabledFlag());
        node.setSecurityLevel(entity.getSecurityLevel());
        node.setIntegrationType(entity.getIntegrationType());
        node.setDescription(entity.getDescription());
        node.setRemark(entity.getRemark());
        node.setSortOrder(entity.getSortOrder());
        node.setLastUpdatedBy(entity.getLastUpdatedBy());
        node.setLastUpdateDate(entity.getLastUpdateDate());
        return node;
    }

    private BusinessModuleExtFieldResponse toFieldResponse(BusinessModuleExtField entity) {
        BusinessModuleExtFieldResponse response = new BusinessModuleExtFieldResponse();
        response.setFieldId(entity.getFieldId());
        response.setFieldCode(entity.getFieldCode());
        response.setModuleCode(entity.getModuleCode());
        response.setFieldScope(entity.getFieldScope());
        response.setApplicationFunctions(parseApplicationFunctions(entity.getApplicationFunctions()));
        response.setExtAttribute(entity.getExtAttribute());
        response.setFieldName(entity.getFieldName());
        response.setEnglishFieldName(entity.getEnglishFieldName());
        response.setDataType(entity.getDataType());
        response.setQueryFlag(entity.getQueryFlag());
        response.setRequiredFlag(entity.getRequiredFlag());
        response.setEnabledFlag(entity.getEnabledFlag());
        response.setSortOrder(entity.getSortOrder());
        response.setLastUpdateDate(entity.getLastUpdateDate());
        return response;
    }

    private BusinessModuleParentOptionResponse toParentOption(BusinessModule module) {
        BusinessModuleParentOptionResponse response = new BusinessModuleParentOptionResponse();
        response.setCode(module.getModuleCode());
        response.setDescription(StringUtils.hasText(module.getDescription()) ? module.getDescription() : module.getModuleName());
        response.setSourceType("BUSINESS_MODULE");
        return response;
    }

    private BusinessModuleParentOptionResponse toParentOption(DocumentTypeConfig documentType) {
        BusinessModuleParentOptionResponse response = new BusinessModuleParentOptionResponse();
        response.setCode(documentType.getDocTypeCode());
        response.setDescription(documentType.getDocTypeDescription());
        response.setSourceType("DOCUMENT_TYPE");
        return response;
    }

    private record TreeMeta(Integer levelNum, String ancestorPath) {}
}
