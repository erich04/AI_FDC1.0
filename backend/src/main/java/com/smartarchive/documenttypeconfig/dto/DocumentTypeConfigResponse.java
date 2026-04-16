package com.smartarchive.documenttypeconfig.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentTypeConfigResponse {
    private Long documentTypeId;
    private String docTypeCode;
    private String docTypeDescription;
    private String enableFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
