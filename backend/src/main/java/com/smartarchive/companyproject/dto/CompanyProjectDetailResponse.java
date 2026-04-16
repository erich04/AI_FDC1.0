package com.smartarchive.companyproject.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CompanyProjectDetailResponse {
    private Long id;
    /** 公司编码（domain: company_code） */
    private String companyProjectCode;
    /** 公司名称（domain: company_name） */
    private String companyProjectName;
    private String countryCode;
    private String managementArea;
    private String companyTag;
    private String enabledFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private List<CompanyProjectLineResponse> lines;
}
