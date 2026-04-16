package com.smartarchive.workspace.dto;

import lombok.Builder;

@Builder
public record WorkspaceImportQueryResultRecordResponse(
    Long archiveId,
    String businessCode,
    String companyProjectName,
    String archiveTypeCode,
    String beginPeriod,
    String endPeriod,
    String archiveDestination,
    String originPlace,
    String documentOrganizationCode,
    String archiveStatus,
    String documentName,
    String documentDate,
    String dutyPerson,
    String dutyDepartment,
    String carrierTypeCode,
    String documentVisibility,
    String sourceSystem,
    String securityLevelName,
    String remark,
    String creationDate,
    String createdBy
) {
}
