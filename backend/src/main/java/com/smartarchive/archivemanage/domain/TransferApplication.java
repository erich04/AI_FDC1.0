package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_application_t")
public class TransferApplication {
    @TableId(value = "application_id", type = IdType.AUTO)
    private Long applicationId;
    private String applicationNumber;
    private Long applicant;
    private LocalDateTime applicationDate;
    private String department;
    private String documentTypeCode;
    private String applyMethod;
    private String expressType;
    private String expressNumber;
    private Long documentRecipient;
    private String handoverForm;
    private String carrierType;
    /** 申请状态（快码） */
    private String applicationStatus;
    /** 申请状态（草稿/已提交） */
    private String status;
    /** 差异原因（快码） */
    private String diffReasonCode;
    private String applicationDescription;
    private String enableFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private String sysDescription;
    private String lastUpdateTraceId;
    private Integer lastUpdateVersion;
    private Long tenantid;
    /** 审批通过后是否已写入档案库 Y/N */
    private String archivesMaterialized;
}
