package com.smartarchive.companyinfo.dto;

import lombok.Data;

@Data
public class CompanyTagResponse {
    private Long tagId;
    private String tagValue;
    private String enabledFlag;
}
