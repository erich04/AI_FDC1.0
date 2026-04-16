package com.smartarchive.documenttypeconfig.dto;

import lombok.Data;

@Data
public class DocumentTypeConfigQueryCommand {
    private String docTypeCode;
    private String docTypeDescription;
    private String enableFlag = "Y";
    private Long tenantid;
}
