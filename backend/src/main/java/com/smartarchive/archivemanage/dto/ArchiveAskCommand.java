package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArchiveAskCommand {
    @NotBlank
    private String question;
    private String busiModuleCode;
    private String companyProjectCode;
}
