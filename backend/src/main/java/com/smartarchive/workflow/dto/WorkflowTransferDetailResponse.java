package com.smartarchive.workflow.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class WorkflowTransferDetailResponse {
    private String transferorName;
    private String assigneeId;
    private String transferMethod;
    private String logisticsCompany;
    private String trackingNumber;
    private String remark;
    private String applicationNumber;
    private List<TransferDocumentItem> documents = new ArrayList<>();

    @Data
    public static class TransferDocumentItem {
        private String documentTypeCode;
        private String businessCode;
        private String documentOrganizationCode;
        private Map<String, String> extFields;
    }
}
