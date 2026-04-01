package com.smartarchive.documentorganization.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_document_organization_t")
public class DocumentOrganization {
    @TableId(value = "document_organization_id", type = IdType.AUTO)
    private Long id;
    private String documentOrganizationCode;
    private String documentOrganizationName;
    private String description;
    private String countryCode;
    private String cityCode;
    @TableField("enable_flag")
    private String enabledFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
