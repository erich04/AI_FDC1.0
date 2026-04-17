package com.smartarchive.archive.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class BorrowOrderDto {
    private Long id;
    private String orderNo;
    private String userName;
    private String userDepartment;
    private String applicantName;
    private LocalDateTime applyTime;
    private String purpose;
    private String reason;
    private String reasonAttachment;
    private String approvalComment;
    private String demandApprover;
    private String demandReviewer;
    private String demandAnalyst;
    private List<String> ccUsers;
    private String status;
    private String workflowInstanceId;
    private String currentHandler;
    private List<BorrowOrderDetailDto> details;
}
