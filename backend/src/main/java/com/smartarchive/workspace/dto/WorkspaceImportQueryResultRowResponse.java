package com.smartarchive.workspace.dto;

import lombok.Builder;

@Builder
public record WorkspaceImportQueryResultRowResponse(
    Long resultId,
    Integer queryRowNo,
    Long archiveId,
    String docId,
    String businessCode,
    String documentName,
    String docStatus,
    String lifecycleStatus
) {
}
