package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_four_attr_inspection_t")
public class FourAttrInspection {
    @TableId(value = "inspection_id", type = IdType.AUTO)
    private Long inspectionId;
    private String inspectionName;
    private String inspectionStage;
    private String dataPackageSpec;
    private String metadataSpec;
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
