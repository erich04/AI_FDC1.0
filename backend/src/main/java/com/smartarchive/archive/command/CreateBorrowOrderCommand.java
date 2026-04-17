package com.smartarchive.archive.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CreateBorrowOrderCommand {
    private String orderNo;
    @NotBlank
    private String userName;
    private String userDepartment;
    @NotBlank
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
    @Valid
    @NotEmpty
    private List<CreateBorrowOrderDetailCommand> details;
}
