package com.smartarchive.businessmodule.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class BusinessModuleExtFieldResponse {
    private Long fieldId;
    private String fieldCode;
    private String moduleCode;
    private String fieldScope;
    private List<String> applicationFunctions;
    private String extAttribute;
    private String fieldName;
    private String englishFieldName;
    private String dataType;
    private String queryFlag;
    private String requiredFlag;
    private String enabledFlag;
    private Integer sortOrder;
    private LocalDateTime lastUpdateDate;
}
