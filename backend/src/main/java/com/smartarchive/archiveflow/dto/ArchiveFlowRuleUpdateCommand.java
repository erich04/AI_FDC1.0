package com.smartarchive.archiveflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArchiveFlowRuleUpdateCommand {
    @NotBlank(message = "cannot be blank")
    @Size(max = 64, message = "length cannot exceed 64")
    private String busiModuleCode;

    @Size(max = 500, message = "length cannot exceed 500")
    private String customRule;

    @Size(max = 64, message = "length cannot exceed 64")
    private String archiveDestination;

    @NotBlank(message = "cannot be blank")
    @Size(max = 64, message = "length cannot exceed 64")
    private String documentOrganizationCode;

    @NotNull(message = "cannot be null")
    private Integer retentionPeriodYears;

    @NotBlank(message = "cannot be blank")
    @Pattern(regexp = "[YN]", message = "only supports Y or N")
    private String externalDisplayFlag;

    @NotBlank(message = "cannot be blank")
    @Pattern(regexp = "[YN]", message = "only supports Y or N")
    private String defaultFlag;

    @NotBlank(message = "cannot be blank")
    @Pattern(regexp = "[YN]", message = "only supports Y or N")
    private String enabledFlag;
}
