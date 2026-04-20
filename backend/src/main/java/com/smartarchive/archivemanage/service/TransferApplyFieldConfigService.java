package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigResponse;
import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigSaveCommand;
import java.util.Map;

public interface TransferApplyFieldConfigService {
    TransferApplyFieldConfigResponse getByDocumentTypeCode(String documentTypeCode, Long tenantid);
    TransferApplyFieldConfigResponse saveByDocumentTypeCode(String documentTypeCode, TransferApplyFieldConfigSaveCommand command);
    Map<String, Boolean> visibilityMapByDocumentTypeCode(String documentTypeCode, Long tenantid);
}
