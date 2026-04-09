package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_four_attr_inspection_detail_t")
public class FourAttrInspectionDetail {
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;
    private Long inspectionId;
    private String inspectionType;
    private String inspectionCode;
    private String inspectionItem;
    private String inspectionPurpose;
    private String inspectionObject;
    private String inspectionBasisMethod;
    private Integer displayOrder;
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
