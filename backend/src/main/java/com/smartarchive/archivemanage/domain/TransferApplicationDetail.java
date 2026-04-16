package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_application_detail_t")
public class TransferApplicationDetail {
    @TableId(value = "application_detail_id", type = IdType.AUTO)
    private Long applicationDetailId;
    private Long applicationId;
    private String docBusiNo;
    private String docName;
    private String busiModuleCode;
    /** 公司编码（company_code，库字段 company_project_code） */
    private String companyProjectCode;
    private String archPlaceAlpha2Code;
    private String endArchPeriod;
    private String startArchPeriod;
    private String archTypeCode;
    private String carrierType;
    private LocalDate docGenerationDate;
    private BigDecimal archCopies;
    private String remark;
    private String description;
    /** 册号 */
    private String catalogVolumeNo;
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
}
