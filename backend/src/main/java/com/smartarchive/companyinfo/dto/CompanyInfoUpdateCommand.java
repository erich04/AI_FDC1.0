package com.smartarchive.companyinfo.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class CompanyInfoUpdateCommand {
    @NotBlank
    private String companyName;
    private String region;
    private String representativeOffice;
    private String country;
    private String description;
    private List<String> tags;
    private String enabledFlag;
}
