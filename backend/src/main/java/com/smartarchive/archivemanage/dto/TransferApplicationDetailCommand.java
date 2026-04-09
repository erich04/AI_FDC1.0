package com.smartarchive.archivemanage.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferApplicationDetailCommand {
    @NotBlank
    private String docBusiNo;
    @NotBlank
    private String docName;
    @NotBlank
    private String busiModuleCode;
    @NotBlank
    private String companyProjectCode;
    @NotBlank
    private String archPlaceAlpha2Code;
    @NotNull
    private String endArchPeriod;
    @NotNull
    private String startArchPeriod;
    @NotBlank
    private String archTypeCode;
    @NotBlank
    private String carrierType;
    private LocalDate docGenerationDate;
    @NotNull
    @DecimalMin(value = "0.0000000001")
    private BigDecimal archCopies;
    private String remark;
    private String description;
    private String catalogVolumeNo;
    @Valid
    private List<TransferApplicationExtValueCommand> extValues;
}
