package com.smartarchive.archive.dto;

import lombok.Data;

@Data
public class BorrowDocumentQueryRequest {
    private String applicantName;
    private String company;
    private String businessCode;
    private String documentName;
    private String documentType;
    private String orderNo;
    private String status;
}
