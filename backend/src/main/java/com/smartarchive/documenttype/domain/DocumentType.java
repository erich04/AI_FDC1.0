package com.smartarchive.documenttype.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_document_type_t")
public class DocumentType {
    @TableId(value = "busi_module_id", type = IdType.AUTO)
    private Long id;
    private String typeCode;
    private String typeName;
    private String description;
    @TableField("enable_flag")
    private String enabledFlag;
    private String parentCode;
    private Integer levelNum;
    private String ancestorPath;
    private Integer sortOrder;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
