package com.smartarchive.archivemanage.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 移交记录查询条件（与前端字段名一致）。日期区间由前端传字符串数组，服务端解析为起止字段供 SQL 使用。
 */
@Data
public class TransferApplicationRecordQuery {

    /** 文档业务编码（模糊） */
    private String docBusiNo;
    /** 公司/项目编码 */
    private String companyProjectCode;
    /** 业务模块编码 */
    private String busiModuleCode;
    /** 档期区间 [start, end]，yyyy-MM-dd */
    private List<String> archPeriodRange;
    /** 申请人用户 ID */
    private Long applicant;
    /** 申请单号（模糊） */
    private String applicationNumber;
    /** 申请日期区间 [start, end]，yyyy-MM-dd */
    private List<String> applicationDateRange;
    /** 申请状态（快码） */
    private String applicationStatus;
    /** 载体类型（快码） */
    private String carrierType;
    /** 差异原因（快码） */
    private String diffReasonCode;
    /** 移交方式（快码） */
    private String applyMethod;
    /** 邮寄方式（快码） */
    private String expressType;
    /** 邮寄单号（模糊） */
    private String expressNumber;
    /** 接收人用户 ID */
    private Long documentRecipient;
    /** 册号（模糊，匹配明细） */
    private String catalogVolumeNo;

    @JsonIgnore
    private LocalDateTime applicationDateStart;
    @JsonIgnore
    private LocalDateTime applicationDateEnd;
    @JsonIgnore
    private String archPeriodStart;
    @JsonIgnore
    private String archPeriodEnd;
}
