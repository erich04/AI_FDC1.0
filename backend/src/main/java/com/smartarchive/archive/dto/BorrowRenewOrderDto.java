package com.smartarchive.archive.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class BorrowRenewOrderDto {
    private Long id;
    private String renewOrderNo;
    private String sourceOrderNo;
    private String userName;
    private String userDepartment;
    private String applicantName;
    private LocalDateTime applyTime;
    private String purpose;
    private String reason;
    private String reasonAttachment;
    private String reviewer;
    private String handler;
    private List<String> ccUsers;
    private String status;
    private String workflowInstanceId;
    private String currentHandler;
    private List<BorrowRenewDetailDto> details;
}
