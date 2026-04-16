package com.smartarchive.common.audit.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OperationAuditAttachment {
    /** fdc_file_t.file_id */
    private Long fileId;
    private String fileName;
    /** 与 fdc_file_t.file_path 一致，兼容旧数据下载 */
    private String storageKey;
    private Long fileSize;

    public OperationAuditAttachment(Long fileId, String fileName, String storageKey, Long fileSize) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.storageKey = storageKey;
        this.fileSize = fileSize;
    }
}
