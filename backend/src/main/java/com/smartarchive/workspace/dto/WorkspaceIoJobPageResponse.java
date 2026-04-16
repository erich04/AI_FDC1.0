package com.smartarchive.workspace.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkspaceIoJobPageResponse {
    private List<WorkspaceIoJobSummaryResponse> records;
    private Long total;
    private Integer pages;
    private Integer page;
    private Integer pageSize;
}

