package com.smartarchive.archivemanage.dto;

import lombok.Data;

@Data
public class FourAttrInspectionQueryCommand {
    private String inspectionName;
    private String inspectionStage;
    private String enableFlag = "Y";
    private Long tenantid;
}
