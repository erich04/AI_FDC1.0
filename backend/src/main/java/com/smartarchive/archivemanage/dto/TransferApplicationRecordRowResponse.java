package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/** 移交记录查询列表行 */
@Data
@Builder
public class TransferApplicationRecordRowResponse {
    private Long applicationId;
    private String applicationNumber;
    private String busiModuleCode;
    private String busiModuleName;
    private Long applicant;
    /** 申请人展示名（无用户中心时占位） */
    private String applicantName;
    private LocalDateTime applicationDate;
    private String applicationStatus;
    private Long documentRecipient;
    private String documentRecipientName;
    private String expressType;
    private String expressNumber;
}
