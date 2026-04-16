package com.smartarchive.documenttype.service;

import com.smartarchive.documenttype.dto.DocumentTypeCreateCommand;
import com.smartarchive.documenttype.dto.DocumentTypePermissionPreviewResponse;
import com.smartarchive.documenttype.dto.DocumentTypeTreeNodeResponse;
import com.smartarchive.documenttype.dto.DocumentTypeUpdateCommand;
import com.smartarchive.archivemanage.dto.LabelValueOption;
import java.util.List;

public interface DocumentTypeService {
    List<DocumentTypeTreeNodeResponse> listTree();
    DocumentTypeTreeNodeResponse getDetail(String typeCode);
    DocumentTypeTreeNodeResponse create(DocumentTypeCreateCommand command);
    DocumentTypeTreeNodeResponse update(String typeCode, DocumentTypeUpdateCommand command);
    void delete(String typeCode);
    DocumentTypePermissionPreviewResponse getPermissionPreview();
    List<LabelValueOption> listLevel3Modules(String documentTypeCode);
}
