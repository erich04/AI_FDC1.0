package com.smartarchive.common.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smartarchive.common.audit.domain.OperationAuditLogAttach;
import com.smartarchive.common.audit.domain.OperationAuditRecord;
import com.smartarchive.common.audit.dto.AuditRecordResponse;
import com.smartarchive.common.audit.dto.OperationAuditAttachment;
import com.smartarchive.common.audit.mapper.OperationAuditLogAttachMapper;
import com.smartarchive.common.audit.mapper.OperationAuditRecordMapper;
import com.smartarchive.common.audit.service.OperationAuditService;
import com.smartarchive.file.domain.FdcFile;
import com.smartarchive.file.mapper.FdcFileMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationAuditServiceImpl implements OperationAuditService {
    private static final long DEFAULT_TENANT_ID = 1L;

    private final OperationAuditRecordMapper operationAuditRecordMapper;
    private final OperationAuditLogAttachMapper operationAuditLogAttachMapper;
    private final FdcFileMapper fdcFileMapper;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void record(String moduleCode,
                       String moduleName,
                       String businessType,
                       String businessKey,
                       String operationType,
                       String operationSummary,
                       Object beforeSnapshot,
                       Object afterSnapshot,
                       Long operatorId,
                       String operatorName) {
        record(moduleCode, moduleName, businessType, businessKey, operationType, operationSummary,
            beforeSnapshot, afterSnapshot, operatorId, operatorName, null, null);
    }

    @Override
    public void record(String moduleCode,
                       String moduleName,
                       String businessType,
                       String businessKey,
                       String operationType,
                       String operationSummary,
                       Object beforeSnapshot,
                       Object afterSnapshot,
                       Long operatorId,
                       String operatorName,
                       String operationRemark,
                       List<OperationAuditAttachment> auditAttachments) {
        LocalDateTime now = LocalDateTime.now();
        long opId = operatorId == null ? 0L : operatorId;
        OperationAuditRecord record = new OperationAuditRecord();
        record.setTenantid(DEFAULT_TENANT_ID);
        record.setObjectId(parseNumericBusinessKeyOrZero(businessKey));
        record.setObjectType(truncate(moduleCode, 30));
        record.setOperatedBy(opId);
        record.setOperationType(truncate(operationType, 30));
        record.setOpContent(buildOpContentJson(
            moduleName,
            businessType,
            businessKey,
            operationSummary,
            beforeSnapshot,
            afterSnapshot,
            operatorName,
            operationRemark,
            auditAttachments
        ));
        record.setOperationTime(now);
        record.setCreatedBy(opId);
        record.setCreationDate(now);
        record.setLastUpdatedBy(null);
        record.setLastUpdateDate(now);
        record.setLastUpdateVersion(0);
        operationAuditRecordMapper.insert(record);
        Long auditLogId = record.getAuditLogId();
        if (auditLogId != null && auditAttachments != null && !auditAttachments.isEmpty()) {
            int sort = 0;
            for (OperationAuditAttachment att : auditAttachments) {
                if (att == null || att.getFileId() == null || att.getFileId() <= 0) {
                    continue;
                }
                OperationAuditLogAttach link = new OperationAuditLogAttach();
                link.setTenantid(DEFAULT_TENANT_ID);
                link.setAuditLogId(auditLogId);
                link.setFileId(att.getFileId());
                link.setSortOrder(sort++);
                link.setDeleteFlag("N");
                link.setCreatedBy(opId);
                link.setCreationDate(now);
                operationAuditLogAttachMapper.insert(link);
            }
        }
    }

    @Override
    public List<AuditRecordResponse> listByModule(String moduleCode) {
        if (moduleCode == null || moduleCode.isBlank()) {
            return List.of();
        }
        return operationAuditRecordMapper.selectList(new LambdaQueryWrapper<OperationAuditRecord>()
                .eq(OperationAuditRecord::getObjectType, truncate(moduleCode, 30))
                .orderByDesc(OperationAuditRecord::getOperationTime)
                .last("limit 20"))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public List<AuditRecordResponse> listByModuleAndBusinessKey(String moduleCode, String businessKey) {
        if (moduleCode == null || moduleCode.isBlank() || businessKey == null || businessKey.isBlank()) {
            return List.of();
        }
        String mod = truncate(moduleCode.trim(), 30);
        String bk = businessKey.trim();
        LambdaQueryWrapper<OperationAuditRecord> w = new LambdaQueryWrapper<OperationAuditRecord>()
            .eq(OperationAuditRecord::getObjectType, mod)
            .orderByDesc(OperationAuditRecord::getOperationTime)
            .last("limit 100");
        // 应归档等数字主键：优先按 object_id 命中；兼容仅 JSON 里有 businessKey 或历史 object_id=0 的行
        if (bk.matches("^[0-9]+$")) {
            try {
                long oid = Long.parseLong(bk);
                w.and(q -> q.eq(OperationAuditRecord::getObjectId, oid)
                    .or()
                    .apply("op_content::jsonb->>'businessKey' = {0}", bk));
            } catch (NumberFormatException ex) {
                w.apply("op_content::jsonb->>'businessKey' = {0}", bk);
            }
        } else {
            w.apply("op_content::jsonb->>'businessKey' = {0}", bk);
        }
        return operationAuditRecordMapper.selectList(w)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AuditRecordResponse toResponse(OperationAuditRecord record) {
        AuditRecordResponse response = new AuditRecordResponse();
        response.setId(record.getAuditLogId());
        response.setModuleCode(record.getObjectType());
        response.setOperationType(record.getOperationType());
        response.setOperatorId(record.getOperatedBy());
        response.setOperationTime(record.getOperationTime());
        try {
            JsonNode root = objectMapper.readTree(record.getOpContent());
            response.setModuleName(textOrNull(root, "moduleName"));
            response.setBusinessType(textOrNull(root, "businessType"));
            response.setBusinessKey(textOrNull(root, "businessKey"));
            response.setOperationSummary(textOrNull(root, "summary"));
            response.setBeforeSnapshot(jsonNodeToNullableString(root.get("before")));
            response.setAfterSnapshot(jsonNodeToNullableString(root.get("after")));
            response.setOperatorName(textOrNull(root, "operatorName"));
            response.setOperationRemark(textOrNull(root, "operationRemark"));
            List<OperationAuditAttachment> fromDb = loadAuditAttachmentsFromDb(record.getAuditLogId());
            if (fromDb != null && !fromDb.isEmpty()) {
                response.setAuditAttachments(fromDb);
            } else {
                JsonNode att = root.get("auditAttachments");
                if (att != null && att.isArray()) {
                    List<OperationAuditAttachment> list = new ArrayList<>();
                    for (JsonNode n : att) {
                        if (n == null || !n.isObject()) {
                            continue;
                        }
                        Long fid = parseLongNode(n.get("fileId"));
                        String fn = textOrNull(n, "fileName");
                        String sk = textOrNull(n, "storageKey");
                        Long sz = n.get("fileSize") != null && n.get("fileSize").isNumber() ? n.get("fileSize").longValue() : null;
                        if ((fid != null && fid > 0) || (sk != null && !sk.isBlank())) {
                            list.add(new OperationAuditAttachment(fid, fn, sk, sz));
                        }
                    }
                    response.setAuditAttachments(list.isEmpty() ? null : list);
                }
            }
        } catch (Exception ignored) {
            response.setOperationSummary(null);
        }
        response.setOperationTypeName(resolveOperationTypeName(record.getOperationType()));
        return response;
    }

    private String resolveOperationTypeName(String operationType) {
        if (operationType == null || operationType.isBlank()) {
            return operationType;
        }
        String code = operationType.trim();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                """
                select item_code, item_name
                  from fdc_dict_item_t
                 where category_code = 'LOG_OPERATION_TYPE'
                   and coalesce(enable_flag,'Y') = 'Y'
                   and coalesce(delete_flag,'N') = 'N'
                """
            );
            Map<String, String> map = new HashMap<>();
            for (Map<String, Object> row : rows) {
                if (row == null) continue;
                Object c = row.get("item_code");
                Object n = row.get("item_name");
                if (c != null && n != null) {
                    map.put(String.valueOf(c).trim(), String.valueOf(n).trim());
                }
            }
            return map.getOrDefault(code, code);
        } catch (Exception ex) {
            return code;
        }
    }

    private List<OperationAuditAttachment> loadAuditAttachmentsFromDb(Long auditLogId) {
        if (auditLogId == null) {
            return null;
        }
        List<OperationAuditLogAttach> links = operationAuditLogAttachMapper.selectList(
            new LambdaQueryWrapper<OperationAuditLogAttach>()
                .eq(OperationAuditLogAttach::getAuditLogId, auditLogId)
                .eq(OperationAuditLogAttach::getDeleteFlag, "N")
                .orderByAsc(OperationAuditLogAttach::getSortOrder));
        if (links.isEmpty()) {
            return null;
        }
        List<Long> fileIds = links.stream().map(OperationAuditLogAttach::getFileId).filter(Objects::nonNull).distinct().toList();
        if (fileIds.isEmpty()) {
            return null;
        }
        List<FdcFile> files = fdcFileMapper.selectList(new LambdaQueryWrapper<FdcFile>().in(FdcFile::getFileId, fileIds));
        Map<Long, FdcFile> fileMap = files.stream().collect(Collectors.toMap(FdcFile::getFileId, f -> f, (a, b) -> a));
        List<OperationAuditAttachment> list = new ArrayList<>();
        for (OperationAuditLogAttach link : links) {
            FdcFile f = fileMap.get(link.getFileId());
            if (f == null || !"N".equals(f.getDeleteFlag())) {
                continue;
            }
            list.add(new OperationAuditAttachment(f.getFileId(), f.getFileName(), f.getFilePath(), f.getFileSize()));
        }
        return list.isEmpty() ? null : list;
    }

    private static Long parseLongNode(JsonNode n) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isNumber()) {
            return n.longValue();
        }
        if (n.isTextual()) {
            try {
                return Long.parseLong(n.asText().trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static String textOrNull(JsonNode root, String field) {
        JsonNode n = root.get(field);
        if (n == null || n.isNull() || n.isMissingNode()) {
            return null;
        }
        if (n.isTextual()) {
            return n.asText();
        }
        if (n.isNumber() || n.isBoolean()) {
            return n.asText();
        }
        return null;
    }

    private static String jsonNodeToNullableString(JsonNode n) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isTextual()) {
            return n.asText();
        }
        return n.toString();
    }

    private String buildOpContentJson(String moduleName,
                                      String businessType,
                                      String businessKey,
                                      String operationSummary,
                                      Object beforeSnapshot,
                                      Object afterSnapshot,
                                      String operatorName,
                                      String operationRemark,
                                      List<OperationAuditAttachment> auditAttachments) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            putText(root, "moduleName", moduleName);
            putText(root, "businessType", businessType);
            putText(root, "businessKey", businessKey);
            putText(root, "summary", operationSummary);
            putText(root, "operatorName", operatorName);
            putText(root, "operationRemark", operationRemark);
            root.set("before", toJsonNode(beforeSnapshot));
            root.set("after", toJsonNode(afterSnapshot));
            root.set("auditAttachments", toJsonNode(auditAttachments == null || auditAttachments.isEmpty() ? null : auditAttachments));
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("moduleName", moduleName);
            fallback.put("businessType", businessType);
            fallback.put("businessKey", businessKey);
            fallback.put("summary", operationSummary);
            fallback.put("operatorName", operatorName);
            fallback.put("operationRemark", operationRemark);
            fallback.put("before", beforeSnapshot == null ? null : String.valueOf(beforeSnapshot));
            fallback.put("after", afterSnapshot == null ? null : String.valueOf(afterSnapshot));
            fallback.put("auditAttachments", auditAttachments);
            try {
                return objectMapper.writeValueAsString(fallback);
            } catch (JsonProcessingException e2) {
                return "{}";
            }
        }
    }

    private void putText(ObjectNode root, String key, String value) {
        if (value != null) {
            root.put(key, value);
        } else {
            root.putNull(key);
        }
    }

    private JsonNode toJsonNode(Object value) {
        if (value == null) {
            return objectMapper.nullNode();
        }
        if (value instanceof JsonNode j) {
            return j;
        }
        return objectMapper.valueToTree(value);
    }

    private static long parseNumericBusinessKeyOrZero(String businessKey) {
        if (businessKey == null || !businessKey.matches("^[0-9]+$")) {
            return 0L;
        }
        try {
            return Long.parseLong(businessKey);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }
}
