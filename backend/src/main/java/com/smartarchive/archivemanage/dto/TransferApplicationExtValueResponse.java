package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferApplicationExtValueResponse {
    private String fieldCode;
    private String value;
}
