package com.smartarchive.archivemanage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class TransferApplyFieldConfigSaveCommand {
    @NotNull
    private Long tenantid;
    @Valid
    private List<TransferApplyFieldConfigSaveItemCommand> fields;
}
