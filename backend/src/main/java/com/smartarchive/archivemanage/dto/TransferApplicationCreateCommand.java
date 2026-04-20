package com.smartarchive.archivemanage.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TransferApplicationCreateCommand {
    private Long applicationId;
    @NotBlank
    private String applicationNumber;
    @NotNull
    private Long applicant;
    private LocalDateTime applicationDate;
    private String department;
    @NotBlank
    @JsonAlias("documentTypeCode")
    private String busiModuleCode;
    @NotBlank
    private String applyMethod;
    private String expressType;
    private String expressNumber;
    @NotNull
    private Long documentRecipient;
    private String handoverForm;
    private String carrierType;
    private String applicationStatus;
    private String diffReasonCode;
    private String applicationDescription;
    private String sysDescription;
    @NotNull
    private Long tenantid;
    @NotNull
    @Valid
    private List<TransferApplicationDetailCommand> details;
}
