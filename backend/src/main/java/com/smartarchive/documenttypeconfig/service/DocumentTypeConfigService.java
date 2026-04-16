package com.smartarchive.documenttypeconfig.service;

import com.smartarchive.documenttypeconfig.dto.DocumentTypeConfigQueryCommand;
import com.smartarchive.documenttypeconfig.dto.DocumentTypeConfigResponse;
import com.smartarchive.documenttypeconfig.dto.DocumentTypeConfigSaveCommand;
import java.io.InputStream;
import java.util.List;

public interface DocumentTypeConfigService {
    List<DocumentTypeConfigResponse> list(DocumentTypeConfigQueryCommand command);
    DocumentTypeConfigResponse create(DocumentTypeConfigSaveCommand command);
    DocumentTypeConfigResponse update(Long documentTypeId, DocumentTypeConfigSaveCommand command);
    byte[] exportCsv(DocumentTypeConfigQueryCommand command);
    byte[] exportExcel(DocumentTypeConfigQueryCommand command);
    Integer importCsv(InputStream inputStream, Long tenantid);
    Integer importExcel(InputStream inputStream, Long tenantid);
}
