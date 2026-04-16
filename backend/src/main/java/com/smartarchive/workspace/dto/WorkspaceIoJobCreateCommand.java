package com.smartarchive.workspace.dto;

import lombok.Data;

@Data
public class WorkspaceIoJobCreateCommand {
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
    private String resultArtifactText;
}
