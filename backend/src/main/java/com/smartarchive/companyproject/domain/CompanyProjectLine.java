package com.smartarchive.companyproject.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_company_project_line_t")
public class CompanyProjectLine {
    @TableId(value = "company_project_line_id", type = IdType.AUTO)
    private Long id;
    private String companyProjectCode;
    private Integer lineNo;
    private String orgCategory;
    private String organizationCode;
    private String organizationName;
    private String validFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
