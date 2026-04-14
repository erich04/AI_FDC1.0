package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldCreateCommand;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldUpdateCommand;
import java.util.List;

public interface DocumentTypeExtFieldService {
    List<DocumentTypeExtFieldResponse> listDirect(String busiModuleCode);
    List<DocumentTypeExtFieldResponse> listEffective(String busiModuleCode);
    DocumentTypeExtFieldResponse create(String busiModuleCode, DocumentTypeExtFieldCreateCommand command);
    DocumentTypeExtFieldResponse update(String busiModuleCode, String fieldCode, DocumentTypeExtFieldUpdateCommand command);
    void delete(String busiModuleCode, String fieldCode);
}
