package com.smartarchive.archivemanage.dto;

import lombok.Data;

/** 应归档保存/提交时随操作审计记录的补充说明附件（已上传的暂存文件） */
@Data
public class PendingAuditAttachmentRef {
    /** fdc_file_t主键，保存审计时写入 dc_operation_audit_log_attach_t */
    private Long fileId;
    private String fileName;
    /** 与 fdc_file_t.file_path 一致 */
    private String storageKey;
    private Long fileSize;
}
