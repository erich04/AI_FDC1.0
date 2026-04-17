package com.smartarchive.companyinfo.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CompanyInfoResponse {
    private Long companyId;
    private String companyCode;
    private String companyName;
    private String region;
    private String representativeOffice;
    private String country;
    private String description;
    private List<String> tags;
    private String enabledFlag;
    private LocalDateTime lastUpdateDate;
}
