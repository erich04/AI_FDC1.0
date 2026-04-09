package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferApplicationDetailAttachmentResponse {
    private Long attachmentId;
    private Long applicationId;
    private Long applicationDetailId;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private String remark;
    private LocalDateTime creationDate;
}
