package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferApplyFieldConfigResponse {
    private String documentTypeCode;
    private List<TransferApplyFieldConfigItem> fields;
}
