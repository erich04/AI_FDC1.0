package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferApplicationRecordPageResponse {
    private List<TransferApplicationRecordRowResponse> records;
    private Long total;
    /** 总页数 */
    private Integer pages;
    private Integer page;
    private Integer pageSize;
}
