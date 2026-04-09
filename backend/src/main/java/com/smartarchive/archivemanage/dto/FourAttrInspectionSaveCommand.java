package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FourAttrInspectionSaveCommand {
    @NotBlank
    private String inspectionName;
    @NotBlank
    private String inspectionStage;
    @NotBlank
    private String dataPackageSpec;
    @NotBlank
    private String metadataSpec;
    private String enableFlag = "Y";
    private Long tenantid;
}
