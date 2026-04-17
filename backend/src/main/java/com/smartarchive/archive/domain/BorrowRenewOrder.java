package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_borrow_renew_order_t")
public class BorrowRenewOrder {
    @TableId(value = "borrow_renew_order_id", type = IdType.AUTO)
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
    private String ccUsers;
    private String status;
    private String workflowInstanceId;
    private String currentHandler;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
