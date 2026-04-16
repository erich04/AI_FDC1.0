package com.smartarchive.common.audit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("dc_operation_audit_log_attach_t")
public class OperationAuditLogAttach {
    @TableId(value = "attach_id", type = IdType.AUTO)
    private Long attachId;
    private Long tenantid;
    private Long auditLogId;
    private Long fileId;
    private Integer sortOrder;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
}
