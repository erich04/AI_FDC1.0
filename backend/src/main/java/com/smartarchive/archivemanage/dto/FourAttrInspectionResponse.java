package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FourAttrInspectionResponse {
    private Long inspectionId;
    private String inspectionName;
    private String inspectionStage;
    private String dataPackageSpec;
    private String metadataSpec;
    private String enableFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private List<FourAttrInspectionDetailResponse> details;
}
