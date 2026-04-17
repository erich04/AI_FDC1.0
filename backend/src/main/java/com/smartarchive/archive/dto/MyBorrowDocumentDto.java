package com.smartarchive.archive.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class MyBorrowDocumentDto {
    private String company;
    private String businessCode;
    private String documentName;
    private String documentType;
    private String businessModule;
    private String archivePeriod;
    private String orderNo;
    private String status;
    private LocalDate borrowTime;
    private String attachment;
}
