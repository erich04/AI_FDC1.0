package com.smartarchive.common.audit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_operation_audit_log_t")
public class OperationAuditRecord {
    @TableId(value = "audit_log_id", type = IdType.AUTO)
    private Long auditLogId;
    private Long tenantid;
    private Long objectId;
    /** Mirrors module_code: PENDING_ARCHIVE, COMPANY_PROJECT, … */
    private String objectType;
    private Long operatedBy;
    private String operationType;
    private String opContent;
    private LocalDateTime operationTime;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private Integer lastUpdateVersion;
}
