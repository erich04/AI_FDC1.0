package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Data;

@Data
public class BindPreviewCommand {
    private String bindMode;
    private String keyword;
    private String busiModuleCode;
    private String companyProjectCode;
    private List<Long> archiveIds;

    // Backward-compatible alias for merged branches still using documentTypeCode.
    public String getDocumentTypeCode() {
        return busiModuleCode;
    }

    // Backward-compatible alias for merged branches still using documentTypeCode.
    public void setDocumentTypeCode(String documentTypeCode) {
        this.busiModuleCode = documentTypeCode;
    }
}
