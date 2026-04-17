package com.smartarchive.businessmodule.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class BusinessModuleNodeResponse {
    private Long id;
    private String moduleCode;
    private String moduleName;
    private String parentCode;
    private Integer levelNum;
    private String ancestorPath;
    private String enabledFlag;
    private String securityLevel;
    private String integrationType;
    private String description;
    private String remark;
    private Integer sortOrder;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private List<BusinessModuleNodeResponse> children = new ArrayList<>();
}
