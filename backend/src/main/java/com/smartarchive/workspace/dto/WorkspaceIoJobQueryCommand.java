package com.smartarchive.workspace.dto;

import lombok.Data;

@Data
public class WorkspaceIoJobQueryCommand {
    private String jobType;
    private String dataType;
    private String keyword;
    private String inputFileName;
    private String jobStatus;
    /** EXPORT_QUERY：CSV / EXCEL / PDF */
    private String exportFileFormat;
    private Integer inputTotal;
    private Integer resultTotal;
    private String createdStart;
    private String createdEnd;
    private Integer page = 1;
    private Integer pageSize = 20;
    /**
     * 为 true 时仅返回导入类任务（排除 EXPORT_QUERY 等），由「我的导入」页传入。
     */
    private Boolean importTasksOnly;
}
