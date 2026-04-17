package com.smartarchive.archive.dto;

import lombok.Data;

@Data
public class BorrowOrderQueryRequest {
    private String orderNo;
    private String applicantName;
    private String userName;
    private String status;
    private String company;
    private String businessCode;
    private String documentName;
}
