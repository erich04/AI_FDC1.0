package com.smartarchive.archiveflow.service;

import com.smartarchive.archiveflow.dto.ArchiveFlowRuleCreateCommand;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleDetailResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleOptionResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRulePermissionPreviewResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleSummaryResponse;
import com.smartarchive.archiveflow.dto.ArchiveFlowRuleUpdateCommand;
import java.util.List;

public interface ArchiveFlowRuleService {
    List<ArchiveFlowRuleSummaryResponse> list(String keyword,
                                              String companyProjectCode,
                                              String busiModuleCode,
                                              String documentOrganizationCode,
                                              String enabledFlag);

    ArchiveFlowRuleDetailResponse getDetail(Long id);

    ArchiveFlowRuleDetailResponse create(ArchiveFlowRuleCreateCommand command);

    ArchiveFlowRuleDetailResponse update(Long id, ArchiveFlowRuleUpdateCommand command);

    void delete(Long id);

    List<ArchiveFlowRuleOptionResponse> listCompanyProjectOptions();

    List<ArchiveFlowRuleOptionResponse> listBusinessModuleOptions();

    List<ArchiveFlowRuleOptionResponse> listDocumentOrganizationOptions();

    List<ArchiveFlowRuleOptionResponse> listCityOptions();

    ArchiveFlowRulePermissionPreviewResponse getPermissionPreview();
}
