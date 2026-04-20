package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferApplyFieldConfigSaveItemCommand {
    @NotBlank
    private String fieldCode;
    @NotBlank
    private String visibleFlag;
}
