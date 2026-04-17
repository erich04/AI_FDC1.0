package com.smartarchive.businessmodule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BusinessModuleUpdateCommand {
    @NotBlank
    private String moduleName;
    private String parentCode;
    private String enabledFlag;
    private Integer sortOrder;
    private String securityLevel;
    private String integrationType;
    private String description;
    private String remark;
}
