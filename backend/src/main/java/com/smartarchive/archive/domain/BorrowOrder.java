package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_borrow_order_t")
public class BorrowOrder {
    @TableId(value = "borrow_order_id", type = IdType.AUTO)
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
    private String ccUsers;
    private String status;
    private String workflowInstanceId;
    private String currentHandler;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
