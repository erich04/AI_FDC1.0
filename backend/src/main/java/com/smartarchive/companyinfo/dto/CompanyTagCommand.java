package com.smartarchive.companyinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyTagCommand {
    @NotBlank
    private String tagValue;
    private String enabledFlag;
}
