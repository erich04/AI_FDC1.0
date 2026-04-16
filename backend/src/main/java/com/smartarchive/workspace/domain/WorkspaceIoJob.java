package com.smartarchive.workspace.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_workspace_io_job_t")
public class WorkspaceIoJob {
    @TableId(type = IdType.AUTO)
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
    /** CSV / EXCEL / PDF — artifact stored as UTF-8 text (Excel使用带 BOM 的 CSV 文本). */
    private String exportFileFormat;
    private String resultArtifactText;
    /** 导入结果二进制（如 xlsx）Base64，用于 IMPORT_PENDING_ARCHIVE 等 */
    private String resultArtifactBase64;
    private LocalDateTime artifactExpiresAt;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private Long tenantid;
}
