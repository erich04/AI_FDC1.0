package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_doc_field_config_t")
public class ArchiveExtFieldConfig {
    @TableId(value = "doc_field_config_id", type = IdType.AUTO)
    private Long fieldId;
    private String fieldCode;
    private String documentTypeCode;
    private String fieldName;
    private String fieldType;
    private String dictCategoryCode;
    private String semanticCode;
    private String usageModule;
    private String relatedModuleCode;
    private String relatedField;
    private String requiredFlag;
    @TableField("enable_flag")
    private String enabledFlag;
    private Integer formSortOrder;
    private String queryEnabledFlag;
    private Integer querySortOrder;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
