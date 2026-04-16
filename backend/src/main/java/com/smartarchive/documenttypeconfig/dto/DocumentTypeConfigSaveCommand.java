package com.smartarchive.documenttypeconfig.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentTypeConfigSaveCommand {
    @NotBlank
    private String docTypeCode;
    @NotBlank
    private String docTypeDescription;
    private String enableFlag = "Y";
    private Long tenantid;
}
