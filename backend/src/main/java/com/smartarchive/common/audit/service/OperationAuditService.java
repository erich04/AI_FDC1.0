package com.smartarchive.common.audit.service;

import com.smartarchive.common.audit.dto.AuditRecordResponse;
import com.smartarchive.common.audit.dto.OperationAuditAttachment;
import java.util.List;

public interface OperationAuditService {
    void record(String moduleCode,
                String moduleName,
                String businessType,
                String businessKey,
                String operationType,
                String operationSummary,
                Object beforeSnapshot,
                Object afterSnapshot,
                Long operatorId,
                String operatorName);

    void record(String moduleCode,
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
                List<OperationAuditAttachment> auditAttachments);

    List<AuditRecordResponse> listByModule(String moduleCode);

    List<AuditRecordResponse> listByModuleAndBusinessKey(String moduleCode, String businessKey);
}
