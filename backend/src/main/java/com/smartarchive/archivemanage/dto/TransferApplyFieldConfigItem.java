package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferApplyFieldConfigItem {
    private String fieldCode;
    private String fieldName;
    private String visibleFlag;
    private Integer sortOrder;
}
