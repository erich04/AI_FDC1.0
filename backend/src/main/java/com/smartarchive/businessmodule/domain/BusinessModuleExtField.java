package com.smartarchive.businessmodule.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_business_module_ext_field_t")
public class BusinessModuleExtField {
    @TableId(value = "field_id", type = IdType.AUTO)
    private Long fieldId;
    private String fieldCode;
    private String moduleCode;
    private String fieldScope;
    private String applicationFunctions;
    private String extAttribute;
    private String fieldName;
    private String englishFieldName;
    private String dataType;
    private String queryFlag;
    private String requiredFlag;
    private String enabledFlag;
    private Integer sortOrder;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
