package com.smartarchive.workspace.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkspaceIoJobSummaryResponse {
    private Long jobId;
    private String jobType;
    private String dataType;
    private String jobName;
    private String documentTypeCode;
    private String queryConfigJson;
    private String inputFileName;
    private Integer inputTotal;
    private Integer resultTotal;
    private Long durationMs;
    private String jobStatus;
    private String errorMessage;
    private String failedFileCsv;
    private String exportFileFormat;
    private LocalDateTime artifactExpiresAt;
    /** 列表展示用：含 EXPIRED（导出文件过期）等对 jobStatus 的归一化 */
    private String displayStatus;
    private Boolean exportDownloadable;
    /** 应归档批量导入等：可下载结果 Excel */
    private Boolean resultArtifactDownloadable;
    private LocalDateTime creationDate;
}
