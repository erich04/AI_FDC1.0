package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArchiveAskCommand {
    @NotBlank
    private String question;
    private String busiModuleCode;
    private String companyProjectCode;

    // Backward-compatible alias for merged branches still using documentTypeCode.
    public String getDocumentTypeCode() {
        return busiModuleCode;
    }

    // Backward-compatible alias for merged branches still using documentTypeCode.
    public void setDocumentTypeCode(String documentTypeCode) {
        this.busiModuleCode = documentTypeCode;
    }
}
