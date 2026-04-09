package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FourAttrInspectionDetailResponse {
    private Long detailId;
    private String inspectionType;
    private String inspectionCode;
    private String inspectionItem;
    private String inspectionPurpose;
    private String inspectionObject;
    private String inspectionBasisMethod;
    private Integer displayOrder;
    private String enableFlag;
}
