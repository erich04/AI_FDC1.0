package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferApplicationExtValueCommand {
    @NotBlank
    private String fieldCode;
    private String value;
}
