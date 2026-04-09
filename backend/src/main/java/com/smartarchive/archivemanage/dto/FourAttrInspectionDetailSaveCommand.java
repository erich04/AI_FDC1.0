package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FourAttrInspectionDetailSaveCommand {
    private Long detailId;
    @NotBlank
    private String inspectionType;
    private String inspectionCode;
    private String inspectionItem;
    private String inspectionPurpose;
    private String inspectionObject;
    private String inspectionBasisMethod;
    private Integer displayOrder;
    private String enableFlag = "Y";
}
