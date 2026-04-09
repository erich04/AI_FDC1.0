package com.smartarchive.archivemanage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferApplicationRecordPageCommand {

    @Valid
    private TransferApplicationRecordQuery filter;

    @NotNull
    @Min(1)
    private Integer page = 1;

    @NotNull
    @Min(1)
    @Max(200)
    private Integer pageSize = 20;

    /** 租户 ID；未传时由服务层使用默认租户 */
    private Long tenantid;
}
