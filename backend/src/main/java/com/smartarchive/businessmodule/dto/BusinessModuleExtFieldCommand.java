package com.smartarchive.businessmodule.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class BusinessModuleExtFieldCommand {
    @NotBlank
    private String fieldCode;
    @NotBlank
    private String fieldScope;
    private List<String> applicationFunctions;
    private String extAttribute;
    @NotBlank
    private String fieldName;
    private String englishFieldName;
    @NotBlank
    private String dataType;
    private String queryFlag;
    private String requiredFlag;
    private String enabledFlag;
    private Integer sortOrder;
}
