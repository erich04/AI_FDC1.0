package com.smartarchive.archive.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CreateBorrowRenewOrderCommand {
    private String renewOrderNo;
    @NotBlank
    private String sourceOrderNo;
    @NotBlank
    private String userName;
    private String userDepartment;
    @NotBlank
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
    @Valid
    @NotEmpty
    private List<CreateBorrowRenewDetailCommand> details;
}
