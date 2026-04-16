package com.smartarchive.workspace.service;

import com.smartarchive.workspace.dto.WorkspaceExportArtifactResult;
import com.smartarchive.workspace.dto.WorkspaceImportQueryResultRecordResponse;
import com.smartarchive.workspace.dto.WorkspaceIoJobCreateCommand;
import com.smartarchive.workspace.dto.WorkspaceIoJobPageResponse;
import com.smartarchive.workspace.dto.WorkspaceIoJobQueryCommand;
import com.smartarchive.workspace.dto.WorkspaceIoJobSummaryResponse;
import java.util.List;

public interface WorkspaceIoJobService {
    WorkspaceIoJobSummaryResponse create(WorkspaceIoJobCreateCommand command, long operatorUserId);

    WorkspaceIoJobPageResponse query(WorkspaceIoJobQueryCommand command, long operatorUserId);

    WorkspaceIoJobSummaryResponse get(Long jobId, long operatorUserId);

    void delete(Long jobId, long operatorUserId);

    String getFailedFileCsv(Long jobId, long operatorUserId);

    /** 导出任务生成的文本文件（CSV/Excel 兼容）；写入下载审计 */
    WorkspaceExportArtifactResult downloadExportArtifact(Long jobId, long operatorUserId);

    /** 应归档批量导入结果 xlsx（Base64 落库）；写入下载审计 */
    WorkspaceExportArtifactResult downloadImportResultArtifact(Long jobId, long operatorUserId);

    /** 文档批量导入查询命中结果（异步任务产物） */
    List<WorkspaceImportQueryResultRecordResponse> listImportQueryResults(Long jobId, long operatorUserId);
}
