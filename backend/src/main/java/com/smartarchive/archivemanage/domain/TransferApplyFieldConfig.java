package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_transfer_apply_field_cfg_t")
public class TransferApplyFieldConfig {
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;
    private String documentTypeCode;
    private String fieldCode;
    private String fieldName;
    private String visibleFlag;
    private Integer sortOrder;
    private String enableFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private String sysDescription;
    private String lastUpdateTraceId;
    private Long tenantid;
}
