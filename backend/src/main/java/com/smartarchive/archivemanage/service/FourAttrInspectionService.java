package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.FourAttrInspectionDetailBatchSaveCommand;
import com.smartarchive.archivemanage.dto.FourAttrInspectionQueryCommand;
import com.smartarchive.archivemanage.dto.FourAttrInspectionResponse;
import com.smartarchive.archivemanage.dto.FourAttrInspectionSaveCommand;
import java.io.InputStream;
import java.util.List;

public interface FourAttrInspectionService {
    List<FourAttrInspectionResponse> list(FourAttrInspectionQueryCommand command);
    FourAttrInspectionResponse detail(Long inspectionId, Long tenantid);
    FourAttrInspectionResponse create(FourAttrInspectionSaveCommand command);
    FourAttrInspectionResponse update(Long inspectionId, FourAttrInspectionSaveCommand command);
    FourAttrInspectionResponse saveDetails(FourAttrInspectionDetailBatchSaveCommand command);
    byte[] exportCsv(FourAttrInspectionQueryCommand command);
    Integer importCsv(InputStream inputStream, Long tenantid);
}
