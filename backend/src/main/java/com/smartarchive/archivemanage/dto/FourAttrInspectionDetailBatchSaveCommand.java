package com.smartarchive.archivemanage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class FourAttrInspectionDetailBatchSaveCommand {
    @NotNull
    private Long inspectionId;
    private Long tenantid;
    @Valid
    private List<FourAttrInspectionDetailSaveCommand> details = new ArrayList<>();
}
