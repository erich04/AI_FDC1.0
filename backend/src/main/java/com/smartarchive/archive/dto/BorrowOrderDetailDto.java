package com.smartarchive.archive.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BorrowOrderDetailDto {
    private Long id;
    private Long borrowOrderId;
    private String businessCode;
    private String documentName;
    private String company;
    private String documentType;
    private String description;
    private String demandType;
    private Boolean needReturn;
    private LocalDate expectedReturnDate;
    private String lendingApprover;
    private String lendingRemark;
    private String handler;
    private String handlerRemark;
    private LocalDateTime createdAt;
}
